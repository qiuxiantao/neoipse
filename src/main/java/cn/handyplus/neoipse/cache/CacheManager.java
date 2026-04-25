package cn.handyplus.neoipse.cache;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.util.ConfigCacheUtil;
import cn.handyplus.neoipse.util.DataSourceManager;
import cn.handyplus.neoipse.strategy.IpDataSource;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理器
 * 负责处理缓存的存储、清理和大小限制
 *
 * @author 滔天
 */
public class CacheManager {

    /**
     * 单例实例
     */
    private static final CacheManager INSTANCE = new CacheManager();

    /**
     * 地域信息缓存（使用ConcurrentHashMap）
     */
    private final Map<String, String> ipRegionCache = new ConcurrentHashMap<>();

    /**
     * 缓存时间戳记录
     */
    private final Map<String, Long> cacheTimestampMap = new ConcurrentHashMap<>();

    /**
     * 缓存最大大小
     */
    private int maxCacheSize = 1000;

    /**
     * 缓存过期时间（毫秒）
     */
    private static final long CACHE_EXPIRE_TIME = 3600000L; // 1小时

    /**
     * 清理计数器，用于控制清理频率
     */
    private int cleanCounter = 0;

    /**
     * 清理阈值，当操作次数达到此值时执行清理
     */
    private static final int CLEAN_THRESHOLD = 100;

    /**
     * 当缓存超过大小时，删除的条目数量
     */
    private static final int CLEAN_BATCH_SIZE = 10;

    /**
     * 预热线程池
     */
    private final ScheduledExecutorService preheatExecutor = Executors.newScheduledThreadPool(2);

    /**
     * 预热完成标记
     */
    private volatile boolean preheatCompleted = false;

    /**
     * 私有构造方法
     */
    private CacheManager() {
    }

    /**
     * 获取单例实例
     *
     * @return 缓存管理器实例
     */
    public static CacheManager getInstance() {
        return INSTANCE;
    }

    /**
     * 设置缓存最大大小
     *
     * @param maxCacheSize 缓存最大大小
     */
    public void setMaxCacheSize(int maxCacheSize) {
        if (maxCacheSize > 0) {
            this.maxCacheSize = maxCacheSize;
        }
    }

    /**
     * 预热缓存
     * 在服务器启动时预加载常用IP的地域信息
     */
    public void preheatCache() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 开始预热缓存...");

        preheatExecutor.submit(() -> {
            try {
                // 获取预热IP列表
                List<String> preheatIps = getPreheatIps();

                if (preheatIps.isEmpty()) {
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 没有配置预热IP，跳过缓存预热");
                    preheatCompleted = true;
                    return;
                }

                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在预热 " + preheatIps.size() + " 个IP的缓存...");

                // 获取配置的数据源
                String configuredDataSource = ConfigCacheUtil.getString("dataSource", "fallback");
                IpDataSource dataSource = getConfiguredDataSource(configuredDataSource);

                if (dataSource == null) {
                    MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 无法找到配置的数据源: " + configuredDataSource + "，无法预热缓存");
                    preheatCompleted = true;
                    return;
                }

                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 使用数据源: " + configuredDataSource);

                // 预热每个IP
                int successCount = 0;
                for (String ip : preheatIps) {
                    try {
                        String region = dataSource.getRegion(ip);
                        if (region != null && !region.isEmpty()) {
                            // 直接使用put方法添加缓存，不触发清理
                            ipRegionCache.put(ip, region);
                            cacheTimestampMap.put(ip, System.currentTimeMillis());
                            successCount++;
                        }

                        // 避免请求过快
                        Thread.sleep(100);
                    } catch (Exception e) {
                        // 单个IP失败不影响其他IP
                    }
                }

                MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 缓存预热完成！成功预热 " + successCount + "/" + preheatIps.size() + " 个IP");
                preheatCompleted = true;

            } catch (Exception e) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 缓存预热失败: " + e.getMessage());
                preheatCompleted = true;
            }
        });
    }

    /**
     * 获取配置的数据源
     *
     * @param configuredDataSource 配置的数据源名称
     * @return 数据源实例，如果找不到则返回null
     */
    private IpDataSource getConfiguredDataSource(String configuredDataSource) {
        if (configuredDataSource == null || configuredDataSource.isEmpty()) {
            configuredDataSource = "fallback";
        }

        // 如果是fallback模式，返回第一个可用的数据源
        if ("fallback".equalsIgnoreCase(configuredDataSource)) {
            List<DataSourceManager.DataSourceInfo> dataSources = DataSourceManager.getInstance().getSortedDataSources();
            if (!dataSources.isEmpty()) {
                return dataSources.get(0).getSource();
            }
            return null;
        }

        // 查找配置的数据源
        List<DataSourceManager.DataSourceInfo> dataSources = DataSourceManager.getInstance().getDataSources();
        for (DataSourceManager.DataSourceInfo info : dataSources) {
            if (info.getName().equalsIgnoreCase(configuredDataSource)) {
                return info.getSource();
            }
        }

        // 如果找不到配置的数据源，返回第一个可用的
        dataSources = DataSourceManager.getInstance().getSortedDataSources();
        if (!dataSources.isEmpty()) {
            return dataSources.get(0).getSource();
        }

        return null;
    }

    /**
     * 获取预热IP列表
     *
     * @return 预热IP列表
     */
    private List<String> getPreheatIps() {
        List<String> preheatIps = new ArrayList<>();

        // 添加默认的预热IP（常见的公共DNS服务器）
        preheatIps.add("8.8.8.8");      // Google DNS
        preheatIps.add("8.8.4.4");      // Google DNS
        preheatIps.add("1.1.1.1");      // Cloudflare DNS
        preheatIps.add("1.0.0.1");      // Cloudflare DNS
        preheatIps.add("114.114.114.114"); // 114 DNS
        preheatIps.add("119.29.29.29");   // 腾讯DNS
        preheatIps.add("223.5.5.5");    // 阿里DNS

        // 从配置中读取自定义预热IP
        try {
            List<String> customIps = ConfigCacheUtil.getStringList("cache.preheatIps");
            if (customIps != null && !customIps.isEmpty()) {
                for (String ip : customIps) {
                    if (ip != null && !ip.trim().isEmpty()) {
                        String trimmedIp = ip.trim();
                        if (!preheatIps.contains(trimmedIp)) {
                            preheatIps.add(trimmedIp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 忽略配置读取错误
        }

        return preheatIps;
    }

    /**
     * 检查预热是否完成
     *
     * @return 预热是否完成
     */
    public boolean isPreheatCompleted() {
        return preheatCompleted;
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public String get(String key) {
        // 增加清理计数器
        cleanCounter++;
        // 当计数器达到阈值时执行清理
        if (cleanCounter >= CLEAN_THRESHOLD) {
            cleanCounter = 0;
            clearExpiredCache();
        }

        if (ipRegionCache.containsKey(key)) {
            // 更新时间戳，延长缓存时间
            cacheTimestampMap.put(key, System.currentTimeMillis());
            return ipRegionCache.get(key);
        }
        return null;
    }

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void put(String key, String value) {
        ipRegionCache.put(key, value);
        cacheTimestampMap.put(key, System.currentTimeMillis());

        // 增加清理计数器
        cleanCounter++;
        // 当计数器达到阈值时执行清理
        if (cleanCounter >= CLEAN_THRESHOLD) {
            cleanCounter = 0;
            clearExpiredCache();
        }
    }

    /**
     * 清理过期缓存
     */
    public void clearExpiredCache() {
        long currentTime = System.currentTimeMillis();

        // 创建副本避免ConcurrentModificationException
        List<String> expiredKeys = new ArrayList<>();

        // 先收集过期的键
        for (String key : cacheTimestampMap.keySet()) {
            Long timestamp = cacheTimestampMap.get(key);
            if (timestamp != null && currentTime - timestamp > CACHE_EXPIRE_TIME) {
                expiredKeys.add(key);
            }
        }

        // 然后统一删除
        for (String key : expiredKeys) {
            ipRegionCache.remove(key);
            cacheTimestampMap.remove(key);
        }

        // 检查缓存大小，如果超过限制，删除多个最旧的条目
        if (ipRegionCache.size() > maxCacheSize) {
            // 计算需要删除的条目数量
            int toRemove = Math.min(CLEAN_BATCH_SIZE, ipRegionCache.size() - maxCacheSize);

            // 找出最旧的条目
            List<Map.Entry<String, Long>> entryList = new ArrayList<>(cacheTimestampMap.entrySet());
            // 按时间戳排序
            entryList.sort(Map.Entry.comparingByValue());

            // 删除最旧的条目
            for (int i = 0; i < toRemove; i++) {
                if (i < entryList.size()) {
                    String key = entryList.get(i).getKey();
                    ipRegionCache.remove(key);
                    cacheTimestampMap.remove(key);
                }
            }
        }
    }

    /**
     * 清理所有缓存
     */
    public void clearAllCache() {
        ipRegionCache.clear();
        cacheTimestampMap.clear();
    }

    /**
     * 关闭预热线程池
     */
    public void shutdown() {
        preheatExecutor.shutdown();
        try {
            if (!preheatExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                preheatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            preheatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    public int getCacheSize() {
        return ipRegionCache.size();
    }

}
