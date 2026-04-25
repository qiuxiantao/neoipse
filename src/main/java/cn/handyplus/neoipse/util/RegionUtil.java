package cn.handyplus.neoipse.util;

import org.json.JSONObject;

/**
 * 地域信息工具类
 * 统一处理地域信息相关的公共方法
 *
 * @author 滔天
 */
public class RegionUtil {

    /**
     * 获取未知值的显示文本
     * 根据配置决定显示"未知"还是空字符串
     *
     * @return 未知值显示文本
     */
    public static String getUnknownText() {
        boolean showUnknown = ConfigCacheUtil.getBoolean("unknown", true);
        return showUnknown ? "未知" : "";
    }

    /**
     * 判断值是否为未知值
     *
     * @param value 值
     * @return 是否为未知值
     */
    public static boolean isUnknownValue(String value) {
        if (value == null) {
            return true;
        }
        return value.equals("未知") || value.equals("0") || value.isEmpty();
    }

    /**
     * 获取安全的地域信息字符串
     * 如果值为空或未知，返回配置的 unknown 值（或空字符串）
     *
     * @param value 原始值
     * @return 处理后的字符串
     */
    public static String getSafeString(String value) {
        if (value == null || value.isEmpty() || isUnknownValue(value)) {
            return getUnknownText();
        }
        return value;
    }

    /**
     * 安全获取JSON字符串值
     * 如果不存在或为空返回配置的 unknown 值
     *
     * @param json JSON对象
     * @param key 键名
     * @return 字符串值，如果不存在或为空返回配置的 unknown 值
     */
    public static String getJsonString(JSONObject json, String key) {
        try {
            if (json.has(key)) {
                String value = json.getString(key);
                if (value != null && !value.isEmpty() && !"null".equalsIgnoreCase(value)) {
                    return value.trim();
                }
            }
        } catch (Exception e) {
            // 忽略解析异常
        }
        return getUnknownText();
    }

}
