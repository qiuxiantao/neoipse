package cn.handyplus.neoipse.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 速率限制器
 * 用于管理API调用频率，避免触发第三方API的速率限制
 *
 * @author 滔天
 */
public class RateLimiter {

    /**
     * 单例实例
     */
    private static final RateLimiter INSTANCE = new RateLimiter();

    /**
     * 速率限制存储
     * key: API名称
     * value: 调用时间戳列表
     */
    private final Map<String, Map<Long, Integer>> rateLimits = new ConcurrentHashMap<>();

    /**
     * 私有构造方法
     */
    private RateLimiter() {
    }

    /**
     * 获取单例实例
     *
     * @return 速率限制器实例
     */
    public static RateLimiter getInstance() {
        return INSTANCE;
    }

    /**
     * 检查是否允许调用API
     *
     * @param apiName API名称
     * @param maxCalls 最大调用次数
     * @param timeWindow 时间窗口（毫秒）
     * @return 是否允许调用
     */
    public boolean allow(String apiName, int maxCalls, long timeWindow) {
        long currentTime = System.currentTimeMillis();
        Map<Long, Integer> timeMap = rateLimits.computeIfAbsent(apiName, k -> new ConcurrentHashMap<>());

        // 清理过期的调用记录
        timeMap.keySet().removeIf(timestamp -> currentTime - timestamp > timeWindow);

        // 计算当前时间窗口内的调用次数
        int totalCalls = timeMap.values().stream().mapToInt(Integer::intValue).sum();

        if (totalCalls < maxCalls) {
            // 允许调用，记录调用
            timeMap.put(currentTime, timeMap.getOrDefault(currentTime, 0) + 1);
            return true;
        } else {
            // 超过限制，不允许调用
            return false;
        }
    }

    /**
     * 检查是否允许调用API（默认配置）
     *
     * @param apiName API名称
     * @return 是否允许调用
     */
    public boolean allow(String apiName) {
        // 默认配置：每分钟最多60次调用（每秒1次）
        return allow(apiName, 60, TimeUnit.MINUTES.toMillis(1));
    }

    /**
     * 清理指定API的速率限制记录
     *
     * @param apiName API名称
     */
    public void clear(String apiName) {
        rateLimits.remove(apiName);
    }

    /**
     * 清理所有速率限制记录
     */
    public void clearAll() {
        rateLimits.clear();
    }

    /**
     * 获取指定API的当前调用次数
     *
     * @param apiName API名称
     * @param timeWindow 时间窗口（毫秒）
     * @return 调用次数
     */
    public int getCallCount(String apiName, long timeWindow) {
        long currentTime = System.currentTimeMillis();
        Map<Long, Integer> timeMap = rateLimits.get(apiName);

        if (timeMap == null) {
            return 0;
        }

        // 清理过期的调用记录
        timeMap.keySet().removeIf(timestamp -> currentTime - timestamp > timeWindow);

        // 计算当前时间窗口内的调用次数
        return timeMap.values().stream().mapToInt(Integer::intValue).sum();
    }

}
