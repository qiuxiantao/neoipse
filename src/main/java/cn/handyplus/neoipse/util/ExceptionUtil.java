package cn.handyplus.neoipse.util;

import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.ChatColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异常处理工具类
 * 负责处理和统计异常，提供详细的错误信息
 *
 * @author 滔天
 */
public class ExceptionUtil {

    /**
     * 单例实例
     */
    private static final ExceptionUtil INSTANCE = new ExceptionUtil();

    /**
     * 异常统计
     */
    private final Map<String, Integer> exceptionStats = new ConcurrentHashMap<>();

    /**
     * 私有构造方法
     */
    private ExceptionUtil() {
    }

    /**
     * 获取单例实例
     *
     * @return 异常处理工具实例
     */
    public static ExceptionUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 处理异常
     *
     * @param source 异常来源
     * @param e 异常
     */
    public void handleException(String source, Exception e) {
        // 记录异常统计
        String key = source + ":" + e.getClass().getSimpleName();
        exceptionStats.put(key, exceptionStats.getOrDefault(key, 0) + 1);

        // 打印详细的错误信息
        MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误来源: " + source);
        MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误类型: " + e.getClass().getName());
        MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误信息: " + e.getMessage());

        // 打印堆栈跟踪
        if (e.getStackTrace().length > 0) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 堆栈跟踪:");
            for (int i = 0; i < Math.min(e.getStackTrace().length, 5); i++) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] " + e.getStackTrace()[i].toString());
            }
            if (e.getStackTrace().length > 5) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] ... 更多堆栈信息...");
            }
        }
    }

    /**
     * 处理异常并返回默认值
     *
     * @param source 异常来源
     * @param e 异常
     * @param defaultValue 默认值
     * @param <T> 返回类型
     * @return 默认值
     */
    public <T> T handleException(String source, Exception e, T defaultValue) {
        handleException(source, e);
        return defaultValue;
    }

    /**
     * 打印异常统计信息
     */
    public void printExceptionStats() {
        if (exceptionStats.isEmpty()) {
            MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 没有异常记录");
            return;
        }

        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 异常统计信息:");
        for (Map.Entry<String, Integer> entry : exceptionStats.entrySet()) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] " + entry.getKey() + ": " + entry.getValue() + " 次");
        }
    }

    /**
     * 清理异常统计
     */
    public void clearExceptionStats() {
        exceptionStats.clear();
    }

}
