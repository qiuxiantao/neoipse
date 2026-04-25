package cn.handyplus.neoipse.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public String get(String key) {
        clearExpiredCache();
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
        clearExpiredCache();
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
        
        // 检查缓存大小，如果超过限制，删除最旧的条目
        if (ipRegionCache.size() > maxCacheSize) {
            // 找出最早的时间戳
            String oldestKey = null;
            long oldestTime = Long.MAX_VALUE;
            
            for (Map.Entry<String, Long> entry : cacheTimestampMap.entrySet()) {
                if (entry.getValue() < oldestTime) {
                    oldestTime = entry.getValue();
                    oldestKey = entry.getKey();
                }
            }
            
            // 删除最旧的条目
            if (oldestKey != null) {
                ipRegionCache.remove(oldestKey);
                cacheTimestampMap.remove(oldestKey);
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
     * 获取缓存大小
     *
     * @return 缓存大小
     */
    public int getCacheSize() {
        return ipRegionCache.size();
    }

}
