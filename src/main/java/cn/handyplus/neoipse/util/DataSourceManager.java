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
            // 权重次之
            int weightCompare = Integer.compare(b.getWeight(), a.getWeight());
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
     * 开始健康检查
     */
    private void startHealthCheck() {
        // 每5分钟检查一次数据源健康状态
        scheduler.scheduleAtFixedRate(this::checkDataSourcesHealth, 0, 5, TimeUnit.MINUTES);
    }

    /**
     * 检查数据源健康状态
     */
    private void checkDataSourcesHealth() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 开始检查数据源健康状态...");
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
            dataSourceHealth.put(info.getName(), healthy);
            if (healthy) {
                MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] " + info.getName() + " 数据源健康状态良好");
            } else {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] " + info.getName() + " 数据源健康状态异常");
            }
        } catch (Exception e) {
            dataSourceHealth.put(info.getName(), false);
            MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] " + info.getName() + " 数据源健康检查失败: " + e.getMessage());
        }
    }

    /**
     * 记录数据源查询结果
     *
     * @param dataSourceName 数据源名称
     * @param success        是否成功
     */
    public void recordQueryResult(String dataSourceName, boolean success) {
        SuccessRate rate = successRateMap.getOrDefault(dataSourceName, new SuccessRate());
        rate.record(success);
        successRateMap.put(dataSourceName, rate);
    }

    /**
     * 获取数据源健康状态
     *
     * @param dataSourceName 数据源名称
     * @return 健康状态
     */
    public boolean isDataSourceHealthy(String dataSourceName) {
        return dataSourceHealth.getOrDefault(dataSourceName, false);
    }

    /**
     * 关闭调度线程池
     */
    public void shutdown() {
        scheduler.shutdown();
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
            return total == 0 ? 0 : (double) success / total;
        }
    }

}
