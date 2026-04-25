package cn.handyplus.neoipse.util;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.neoipse.cache.CacheManager;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * IP工具类
 *
 * @author 滔天
 * @since 1.0.0
 */
public class IpUtil {

    /**
     * 缓存管理器
     */
    private static final CacheManager CACHE_MANAGER = CacheManager.getInstance();

    /**
     * 验证管理器
     */
    private static final ValidationManager VALIDATION_MANAGER = ValidationManager.getInstance();

    /**
     * 数据源管理器
     */
    private static final DataSourceManager DATA_SOURCE_MANAGER = DataSourceManager.getInstance();

    /**
     * 获取玩家IP
     *
     * @param player 玩家
     * @return IP
     */
    public static String getIp(Player player) {
        if (player.getAddress() == null) {
            return null;
        }
        return player.getAddress().getAddress().getHostAddress();
    }

    /**
     * 同步获取地域信息
     * 注意：此方法会在调用线程中执行，可能会阻塞
     *
     * @param ip IP
     * @return 地域信息
     */
    public static String getIpRegion(String ip) {
        // 验证IP地址
        ip = VALIDATION_MANAGER.sanitizeIp(ip);
        if (ip == null) {
            return null;
        }

        // 先从缓存获取
        String cachedRegion = CACHE_MANAGER.get(ip);
        if (cachedRegion != null) {
            return cachedRegion;
        }

        // 获取配置的数据源类型
        String dataSourceName = ConfigCacheUtil.getString("dataSource", "fallback");
        
        try {
            // 使用 DataSourceManager 获取数据源实例
            IpDataSource dataSource = DATA_SOURCE_MANAGER.getDataSourceByName(dataSourceName);
            String region = dataSource.getRegion(ip);

            // 缓存结果
            if (region != null) {
                CACHE_MANAGER.put(ip, region);
            }
            return region;
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpUtil.getIpRegion", e, null);
        }
    }

    /**
     * 异步获取地域信息
     * 注意：此方法会在后台线程中执行，不会阻塞主线程
     *
     * @param ip IP
     * @param callback 回调函数，参数为地域信息，为null表示查询失败
     */
    public static void getIpRegionAsync(String ip, Consumer<String> callback) {
        // 验证IP地址
        final String sanitizedIp = VALIDATION_MANAGER.sanitizeIp(ip);
        if (sanitizedIp == null) {
            callback.accept(null);
            return;
        }

        // 先从缓存获取
        String cachedRegion = CACHE_MANAGER.get(sanitizedIp);
        if (cachedRegion != null) {
            callback.accept(cachedRegion);
            return;
        }

        // 获取配置的数据源类型
        final String dataSourceName = ConfigCacheUtil.getString("dataSource", "fallback");
        
        try {
            // 使用 DataSourceManager 获取数据源实例
            IpDataSource dataSource = DATA_SOURCE_MANAGER.getDataSourceByName(dataSourceName);

            // 异步查询
            dataSource.getRegionAsync(sanitizedIp, region -> {
                // 缓存结果
                if (region != null) {
                    CACHE_MANAGER.put(sanitizedIp, region);
                }
                callback.accept(region);
            });
        } catch (Exception e) {
            ExceptionUtil.getInstance().handleException("IpUtil.getIpRegionAsync", e);
            callback.accept(null);
        }
    }

    /**
     * 验证IP是否有效
     *
     * @param ip IP
     * @return 是否有效
     */
    public static boolean isValidIp(String ip) {
        return VALIDATION_MANAGER.isValidIp(ip);
    }

    /**
     * 清理所有IP缓存
     */
    public static void clearAllCache() {
        CACHE_MANAGER.clearAllCache();
    }

    /**
     * 重新加载缓存大小配置
     */
    public static void reloadCacheSize() {
        try {
            int size = ConfigCacheUtil.getInt("cache.maxSize", 1000);
            CACHE_MANAGER.setMaxCacheSize(size);
        } catch (Exception e) {
            CACHE_MANAGER.setMaxCacheSize(1000);
        }
    }

    /**
     * 获取字符串，如果为空则返回"0"
     *
     * @param str 字符串
     * @return 处理后的字符串
     */
    public static String getStr(String str) {
        return StrUtil.isEmpty(str) ? "0" : str;
    }

}

