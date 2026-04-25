package cn.handyplus.neoipse.util;

import cn.handyplus.lib.core.StrUtil;
import org.bukkit.configuration.file.FileConfiguration;
import cn.handyplus.neoipse.NeoIpSee;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置缓存工具类
 *
 * @author 滔天
 * @since 1.0.0
 */
public class ConfigCacheUtil {

    /**
     * 配置缓存
     */
    private static final Map<String, Object> CONFIG_CACHE = new HashMap<>();

    /**
     * 获取配置
     *
     * @param path 路径
     * @param def 默认值
     * @return 配置值
     */
    public static String getString(String path, String def) {
        Object value = CONFIG_CACHE.get(path);
        if (value != null) {
            return String.valueOf(value);
        }
        FileConfiguration config = NeoIpSee.INSTANCE.getConfig();
        String configValue = config.getString(path);
        if (StrUtil.isEmpty(configValue)) {
            configValue = def;
        }
        CONFIG_CACHE.put(path, configValue);
        return configValue;
    }

    /**
     * 获取整数配置
     *
     * @param path 路径
     * @param def 默认值
     * @return 配置值
     */
    public static int getInt(String path, int def) {
        Object value = CONFIG_CACHE.get(path);
        if (value != null) {
            return Integer.parseInt(String.valueOf(value));
        }
        FileConfiguration config = NeoIpSee.INSTANCE.getConfig();
        int configValue = config.getInt(path, def);
        CONFIG_CACHE.put(path, configValue);
        return configValue;
    }

    /**
     * 获取布尔配置
     *
     * @param path 路径
     * @param def 默认值
     * @return 配置值
     */
    public static boolean getBoolean(String path, boolean def) {
        Object value = CONFIG_CACHE.get(path);
        if (value != null) {
            return Boolean.parseBoolean(String.valueOf(value));
        }
        FileConfiguration config = NeoIpSee.INSTANCE.getConfig();
        boolean configValue = config.getBoolean(path, def);
        CONFIG_CACHE.put(path, configValue);
        return configValue;
    }

    /**
     * 获取语言配置
     *
     * @return 语言代码
     */
    public static String getLanguage() {
        return getString("language", "zh_CN");
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        CONFIG_CACHE.clear();
    }

    /**
     * 重载配置
     */
    public static void reloadConfig() {
        // 清除缓存
        clearCache();
        // 重新加载配置文件
        NeoIpSee.INSTANCE.reloadConfig();
        // 重新检查配置
        ConfigCheckUtil.checkAndFixConfig();
    }

}