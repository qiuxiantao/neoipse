package cn.handyplus.neoipse.util;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.neoipse.cache.CacheManager;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.strategy.impl.FallbackIpDataSource;
import cn.handyplus.neoipse.strategy.impl.Ip9DataSource;
import cn.handyplus.neoipse.strategy.impl.IpApiDataSource;
import cn.handyplus.neoipse.strategy.impl.IpInfoDataSource;
import cn.handyplus.neoipse.strategy.impl.IpPlus360DataSource;
import cn.handyplus.neoipse.strategy.impl.IpPlusDataSource;
import cn.handyplus.neoipse.strategy.impl.IpQueryDataSource;
import cn.handyplus.neoipse.strategy.impl.VoreApiDataSource;
import cn.handyplus.neoipse.strategy.impl.WhoisDataSource;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
     * 数据源实例缓存
     */
    private static final Map<String, IpDataSource> DATA_SOURCE_CACHE = new ConcurrentHashMap<>();

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
        String dataSource = ConfigCacheUtil.getString("dataSource", "fallback");
        try {
            // 获取或创建数据源实例
            IpDataSource dataSourceImpl = getDataSourceInstance(dataSource);
            String region = dataSourceImpl.getRegion(ip);

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
        String dataSource = ConfigCacheUtil.getString("dataSource", "fallback");
        try {
            // 获取或创建数据源实例
            IpDataSource dataSourceImpl = getDataSourceInstance(dataSource);

            // 异步查询
            dataSourceImpl.getRegionAsync(sanitizedIp, region -> {
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
     * 获取数据源实例
     *
     * @param dataSource 数据源名称
     * @return 数据源实例
     * @throws Exception 异常
     */
    private static IpDataSource getDataSourceInstance(String dataSource) throws Exception {
        if (DATA_SOURCE_CACHE.containsKey(dataSource)) {
            return DATA_SOURCE_CACHE.get(dataSource);
        }

        IpDataSource dataSourceImpl;

        switch (dataSource.toLowerCase()) {
            case "fallback":
                dataSourceImpl = new FallbackIpDataSource();
                break;
            case "ipplus":
                dataSourceImpl = new IpPlusDataSource();
                break;
            case "ipinfo":
                dataSourceImpl = new IpInfoDataSource();
                break;
            case "ip9":
                dataSourceImpl = new Ip9DataSource();
                break;
            case "ipquery":
                dataSourceImpl = new IpQueryDataSource();
                break;
            case "ipplus360":
                dataSourceImpl = new IpPlus360DataSource();
                break;
            case "ipapi":
                dataSourceImpl = new IpApiDataSource();
                break;
            case "whois":
                dataSourceImpl = new WhoisDataSource();
                break;
            case "voreapi":
                dataSourceImpl = new VoreApiDataSource();
                break;
            default:
                // 默认使用fallback
                dataSourceImpl = new FallbackIpDataSource();
        }

        DATA_SOURCE_CACHE.put(dataSource, dataSourceImpl);
        return dataSourceImpl;
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
        DATA_SOURCE_CACHE.clear();
    }

    /**
     * 重新加载缓存大小配置
     */
    public static void reloadCacheSize() {
        try {
            int size = ConfigCacheUtil.getInt("cache.maxSize", 1000);
            if (size > 0) {
                CACHE_MANAGER.setMaxCacheSize(size);
            }
        } catch (Exception e) {
            // 测试环境中NeoIpSee.INSTANCE可能为null，使用默认值
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
