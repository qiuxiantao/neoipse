package cn.handyplus.neoipse.util;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.strategy.impl.*;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 数据源管理类
 * 负责管理所有数据源，包括健康检查、权重配置和动态调整
 *
 * @author 滔天
 */
public class DataSourceManager {

    /**
     * 单例实例
     */
    private static final DataSourceManager INSTANCE = new DataSourceManager();

    /**
     * 数据源列表
     */
    private final List<DataSourceInfo> dataSources = new ArrayList<>();

    /**
     * 数据源健康状态
     */
    private final Map<String, Boolean> dataSourceHealth = new ConcurrentHashMap<>();

    /**
     * 数据源成功率统计
     */
    private final Map<String, SuccessRate> successRateMap = new ConcurrentHashMap<>();

    /**
     * 数据源连续失败计数
     */
    private final Map<String, Integer> consecutiveFailures = new ConcurrentHashMap<>();

    /**
     * 数据源连续成功计数（用于恢复）
     */
    private final Map<String, Integer> consecutiveSuccesses = new ConcurrentHashMap<>();

    /**
     * 连续失败阈值，达到此值则标记为不健康
     */
    private static final int FAILURE_THRESHOLD = 3;

    /**
     * 连续成功恢复阈值，达到此值则恢复为健康
     */
    private static final int RECOVERY_THRESHOLD = 2;

    /**
     * 调度线程池
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * 私有构造方法
     */
    private DataSourceManager() {
        initDataSources();
        startHealthCheck();
    }

    /**
     * 获取单例实例
     *
     * @return 数据源管理实例
     */
    public static DataSourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化数据源
     */
    private void initDataSources() {
        // 按默认权重添加数据源（权重越高越优先）
        // 注意：IP9和VOREAPI可能无法访问，优先使用其他数据源
        addDataSource("IPPLUS", new IpPlusDataSource(), 100);
        addDataSource("WHOIS", new WhoisDataSource(), 85);
        addDataSource("IPINFO", new IpInfoDataSource(), 80);
        addDataSource("IPQUERY", new IpQueryDataSource(), 75);
        addDataSource("IPPLUS360", new IpPlus360DataSource(), 70);
        addDataSource("IPAPI", new IpApiDataSource(), 65);
        addDataSource("IP9", new Ip9DataSource(), 30);  // IP9暂时无法访问，降低优先级
        addDataSource("VOREAPI", new VoreApiDataSource(), 20);  // VOREAPI可能已不可用
    }

    /**
     * 添加数据源
     *
     * @param name   数据源名称
     * @param source 数据源实例
     * @param weight 权重
     */
    public void addDataSource(String name, IpDataSource source, int weight) {
        dataSources.add(new DataSourceInfo(name, source, weight));
        dataSourceHealth.put(name, true); // 默认健康
        successRateMap.put(name, new SuccessRate());
        consecutiveFailures.put(name, 0);
        consecutiveSuccesses.put(name, 0);
    }

    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    public List<DataSourceInfo> getDataSources() {
        return dataSources;
    }

    /**
     * 根据权重和健康状态获取排序后的数据源
     *
     * @return 排序后的数据源列表
     */
    public List<DataSourceInfo> getSortedDataSources() {
        List<DataSourceInfo> sorted = new ArrayList<>(dataSources);
        // 先按健康状态排序，再按权重排序，最后按成功率排序
        sorted.sort((a, b) -> {
            // 健康状态优先
            boolean aHealthy = dataSourceHealth.getOrDefault(a.getName(), false);
            boolean bHealthy = dataSourceHealth.getOrDefault(b.getName(), false);
            if (aHealthy != bHealthy) {
                return aHealthy ? -1 : 1;
            }
            // 动态权重 = 基础权重 + 成功率调整
            int aDynamicWeight = calculateDynamicWeight(a);
            int bDynamicWeight = calculateDynamicWeight(b);
            int weightCompare = Integer.compare(bDynamicWeight, aDynamicWeight);
            if (weightCompare != 0) {
                return weightCompare;
            }
            // 成功率最后
            double aRate = successRateMap.getOrDefault(a.getName(), new SuccessRate()).getRate();
            double bRate = successRateMap.getOrDefault(b.getName(), new SuccessRate()).getRate();
            return Double.compare(bRate, aRate);
        });
        return sorted;
    }

    /**
     * 计算动态权重
     * 基础权重 + 成功率调整（成功率越高，权重越高）
     *
     * @param info 数据源信息
     * @return 动态权重
     */
    private int calculateDynamicWeight(DataSourceInfo info) {
        int baseWeight = info.getWeight();
        SuccessRate rate = successRateMap.getOrDefault(info.getName(), new SuccessRate());
        double successRate = rate.getRate();

        // 成功率调整：最高可增加50%的权重
        int adjustment = (int) (baseWeight * successRate * 0.5);
        return baseWeight + adjustment;
    }

    /**
     * 开始健康检查
     */
    private void startHealthCheck() {
        // 插件启动5分钟后开始健康检查，每5分钟检查一次
        scheduler.scheduleAtFixedRate(this::checkDataSourcesHealth, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 检查数据源健康状态
     */
    private void checkDataSourcesHealth() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "开始检查数据源健康状态...");
        for (DataSourceInfo info : dataSources) {
            checkDataSourceHealth(info);
        }
    }

    /**
     * 检查单个数据源健康状态
     *
     * @param info 数据源信息
     */
    private void checkDataSourceHealth(DataSourceInfo info) {
        try {
            // 使用一个简单的IP地址进行测试
            String testIp = "8.8.8.8";
            String result = info.getSource().getRegion(testIp);
            boolean healthy = result != null && !result.isEmpty();

            // 更新连续成功/失败计数
            if (healthy) {
                consecutiveSuccesses.put(info.getName(), consecutiveSuccesses.getOrDefault(info.getName(), 0) + 1);
                consecutiveFailures.put(info.getName(), 0);

                // 连续成功达到恢复阈值时，标记为健康
                if (consecutiveSuccesses.get(info.getName()) >= RECOVERY_THRESHOLD && !dataSourceHealth.getOrDefault(info.getName(), false)) {
                    dataSourceHealth.put(info.getName(), true);
                    MessageUtil.sendConsoleMessage(ChatColor.GREEN + info.getName() + " 数据源已恢复健康！");
                }
            } else {
                consecutiveFailures.put(info.getName(), consecutiveFailures.getOrDefault(info.getName(), 0) + 1);
                consecutiveSuccesses.put(info.getName(), 0);

                // 连续失败达到阈值时，标记为不健康
                if (consecutiveFailures.get(info.getName()) >= FAILURE_THRESHOLD) {
                    dataSourceHealth.put(info.getName(), false);
                    MessageUtil.sendConsoleMessage(ChatColor.RED + info.getName() + " 数据源健康状态异常（连续失败 " + consecutiveFailures.get(info.getName()) + " 次）");
                }
            }

            if (dataSourceHealth.getOrDefault(info.getName(), false)) {
                MessageUtil.sendConsoleMessage(ChatColor.GREEN + info.getName() + " 数据源健康状态良好");
            } else {
                MessageUtil.sendConsoleMessage(ChatColor.RED + info.getName() + " 数据源健康状态异常");
            }
        } catch (Exception e) {
            // 更新连续失败计数
            consecutiveFailures.put(info.getName(), consecutiveFailures.getOrDefault(info.getName(), 0) + 1);
            consecutiveSuccesses.put(info.getName(), 0);

            // 连续失败达到阈值时，标记为不健康
            if (consecutiveFailures.get(info.getName()) >= FAILURE_THRESHOLD) {
                dataSourceHealth.put(info.getName(), false);
                MessageUtil.sendConsoleMessage(ChatColor.RED + info.getName() + " 数据源健康检查失败: " + e.getMessage() + "（连续失败 " + consecutiveFailures.get(info.getName()) + " 次）");
            }
        }
    }

    /**
     * 记录数据源查询结果
     * 用于智能切换和动态权重调整
     *
     * @param dataSourceName 数据源名称
     * @param success        是否成功
     */
    public void recordQueryResult(String dataSourceName, boolean success) {
        SuccessRate rate = successRateMap.getOrDefault(dataSourceName, new SuccessRate());
        rate.record(success);
        successRateMap.put(dataSourceName, rate);

        // 更新连续成功/失败计数
        if (success) {
            consecutiveSuccesses.put(dataSourceName, consecutiveSuccesses.getOrDefault(dataSourceName, 0) + 1);
            consecutiveFailures.put(dataSourceName, 0);

            // 连续成功达到恢复阈值时，标记为健康
            if (consecutiveSuccesses.get(dataSourceName) >= RECOVERY_THRESHOLD && !dataSourceHealth.getOrDefault(dataSourceName, false)) {
                dataSourceHealth.put(dataSourceName, true);
            }
        } else {
            consecutiveFailures.put(dataSourceName, consecutiveFailures.getOrDefault(dataSourceName, 0) + 1);
            consecutiveSuccesses.put(dataSourceName, 0);

            // 连续失败达到阈值时，标记为不健康
            if (consecutiveFailures.get(dataSourceName) >= FAILURE_THRESHOLD) {
                dataSourceHealth.put(dataSourceName, false);
            }
        }
    }

    /**
     * 获取数据源健康状态
     *
     * @param dataSourceName 数据源名称
     * @return 健康状态
     */
    public boolean isDataSourceHealthy(String dataSourceName) {
        return dataSourceHealth.getOrDefault(dataSourceName, true);
    }

    /**
     * 获取数据源成功率
     *
     * @param dataSourceName 数据源名称
     * @return 成功率（0.0 - 1.0）
     */
    public double getDataSourceSuccessRate(String dataSourceName) {
        SuccessRate rate = successRateMap.getOrDefault(dataSourceName, new SuccessRate());
        return rate.getRate();
    }

    /**
     * 获取数据源连续失败次数
     *
     * @param dataSourceName 数据源名称
     * @return 连续失败次数
     */
    public int getConsecutiveFailures(String dataSourceName) {
        return consecutiveFailures.getOrDefault(dataSourceName, 0);
    }

    /**
     * 根据名称获取数据源实例
     *
     * @param dataSourceName 数据源名称（不区分大小写）
     * @return 数据源实例，如果没有找到则返回 fallback
     */
    public IpDataSource getDataSourceByName(String dataSourceName) {
        // 特殊处理 fallback
        if ("fallback".equalsIgnoreCase(dataSourceName)) {
            return new cn.handyplus.neoipse.strategy.impl.FallbackIpDataSource();
        }

        // 从现有数据源中查找
        for (DataSourceInfo info : dataSources) {
            if (info.getName().equalsIgnoreCase(dataSourceName)) {
                return info.getSource();
            }
        }

        // 默认返回 fallback
        return new cn.handyplus.neoipse.strategy.impl.FallbackIpDataSource();
    }

    /**
     * 获取可用的数据源名称列表
     *
     * @return 数据源名称列表（包含 fallback）
     */
    public java.util.List<String> getAvailableDataSourceNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        names.add("fallback");
        for (DataSourceInfo info : dataSources) {
            names.add(info.getName().toLowerCase());
        }
        return names;
    }

    /**
     * 关闭调度线程池
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 数据源信息类
     */
    public static class DataSourceInfo {
        private final String name;
        private final IpDataSource source;
        private int weight;

        public DataSourceInfo(String name, IpDataSource source, int weight) {
            this.name = name;
            this.source = source;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public IpDataSource getSource() {
            return source;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    /**
     * 成功率统计类
     */
    private static class SuccessRate {
        private int total = 0;
        private int success = 0;

        public void record(boolean success) {
            total++;
            if (success) {
                this.success++;
            }
            // 限制统计数量，避免内存占用过高
            if (total > 1000) {
                total = 1000;
                this.success = (int) (this.success * 0.9);
            }
        }

        public double getRate() {
            return total == 0 ? 0.0 : (double) success / total;
        }
    }

}
