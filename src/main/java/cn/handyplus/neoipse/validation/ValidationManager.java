package cn.handyplus.neoipse.validation;

import java.util.regex.Pattern;

/**
 * 验证管理器
 * 负责处理输入验证，包括IP地址验证和API密钥验证
 *
 * @author 滔天
 */
public class ValidationManager {

    /**
     * 单例实例
     */
    private static final ValidationManager INSTANCE = new ValidationManager();

    /**
     * IP地址正则表达式（IPv4）
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * IP地址正则表达式（IPv6）
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|^::$|^::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|");

    /**
     * 私有构造方法
     */
    private ValidationManager() {
    }

    /**
     * 获取单例实例
     *
     * @return 验证管理器实例
     */
    public static ValidationManager getInstance() {
        return INSTANCE;
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址
     * @return 是否有效
     */
    public boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches() || IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证并清理IP地址
     *
     * @param ip IP地址
     * @return 清理后的IP地址，无效则返回null
     */
    public String sanitizeIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return null;
        }
        // 去除首尾空格
        ip = ip.trim();
        // 验证IP地址是否有效
        if (!isValidIp(ip)) {
            return null;
        }
        return ip;
    }

    /**
     * 验证API密钥是否有效
     *
     * @param apiKey API密钥
     * @return 是否有效
     */
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("123456");
    }

    /**
     * 掩码API密钥
     *
     * @param apiKey API密钥
     * @return 掩码后的API密钥
     */
    public String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "";
        }
        if (apiKey.length() <= 4) {
            return "****";
        }
        String prefix = apiKey.substring(0, 2);
        String suffix = apiKey.substring(apiKey.length() - 2);
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < apiKey.length() - 4; i++) {
            mask.append('*');
        }
        return prefix + mask + suffix;
    }

}
