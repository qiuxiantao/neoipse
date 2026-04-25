package cn.handyplus.neoipse.util;

import cn.handyplus.lib.core.StrUtil;
import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.ChatColor;

import java.util.regex.Pattern;

/**
 * 安全工具类
 * 负责处理输入验证和API密钥管理
 *
 * @author 滔天
 */
public class SecurityUtil {

    /**
     * 单例实例
     */
    private static final SecurityUtil INSTANCE = new SecurityUtil();

    /**
     * IP地址正则表达式（IPv4）
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * IP地址正则表达式（IPv6）
     */
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|^::$|^::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::$|");

    /**
     * API密钥正则表达式
     */
    private static final Pattern API_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{6,64}$");

    /**
     * 私有构造方法
     */
    private SecurityUtil() {
    }

    /**
     * 获取单例实例
     *
     * @return 安全工具实例
     */
    public static SecurityUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 验证IP地址是否有效
     *
     * @param ip IP地址
     * @return 是否有效
     */
    public boolean isValidIp(String ip) {
        if (StrUtil.isEmpty(ip)) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches() || IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * 验证API密钥是否有效
     *
     * @param apiKey API密钥
     * @return 是否有效
     */
    public boolean isValidApiKey(String apiKey) {
        if (StrUtil.isEmpty(apiKey)) {
            return false;
        }
        return API_KEY_PATTERN.matcher(apiKey).matches();
    }

    /**
     * 清理和验证IP地址
     *
     * @param ip IP地址
     * @return 清理后的IP地址，如果无效返回null
     */
    public String sanitizeIp(String ip) {
        if (StrUtil.isEmpty(ip)) {
            return null;
        }
        ip = ip.trim();
        if (!isValidIp(ip)) {
            try {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 无效的IP地址: " + ip);
            } catch (Exception e) {
                // 忽略测试环境中的异常
            }
            return null;
        }
        return ip;
    }

    /**
     * 清理和验证API密钥
     *
     * @param apiKey API密钥
     * @return 清理后的API密钥，如果无效返回null
     */
    public String sanitizeApiKey(String apiKey) {
        if (StrUtil.isEmpty(apiKey)) {
            return null;
        }
        apiKey = apiKey.trim();
        if (!isValidApiKey(apiKey)) {
            try {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 无效的API密钥: " + apiKey);
            } catch (Exception e) {
                // 忽略测试环境中的异常
            }
            return null;
        }
        return apiKey;
    }

    /**
     * 加密API密钥（简单的掩码处理，用于日志输出）
     *
     * @param apiKey API密钥
     * @return 加密后的API密钥
     */
    public String maskApiKey(String apiKey) {
        if (StrUtil.isEmpty(apiKey)) {
            return "";
        }
        if (apiKey.length() <= 4) {
            return "****";
        }
        int len = apiKey.length();
        String prefix = apiKey.substring(0, 2);
        String suffix = apiKey.substring(len - 2);
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < len - 4; i++) {
            mask.append("*");
        }
        return prefix + mask + suffix;
    }

}
