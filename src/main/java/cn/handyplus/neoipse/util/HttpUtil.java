package cn.handyplus.neoipse.util;

import cn.handyplus.neoipse.http.HttpManager;

import java.util.function.Consumer;

/**
 * HTTP工具类
 * 负责处理所有网络请求，委托给HttpManager实现
 *
 * @author 滔天
 */
public class HttpUtil {

    /**
     * 单例实例
     */
    private static volatile HttpUtil INSTANCE;

    /**
     * HTTP管理器实例（委托）
     */
    private final HttpManager httpManager;

    /**
     * 私有构造方法
     */
    private HttpUtil() {
        this.httpManager = HttpManager.getInstance();
    }

    /**
     * 获取单例实例
     *
     * @return HTTP工具实例
     */
    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 发送GET请求
     *
     * @param urlString URL字符串
     * @return 响应内容
     */
    public String get(String urlString) {
        return httpManager.get(urlString);
    }

    /**
     * 异步发送GET请求
     *
     * @param urlString URL字符串
     * @param callback 回调函数
     */
    public void getAsync(String urlString, Consumer<String> callback) {
        httpManager.getAsync(urlString, callback);
    }

    /**
     * 关闭线程池
     * 委托给HttpManager处理
     */
    public void shutdown() {
        // HttpManager会在NeoIpSee.onDisable()中被关闭
        // 这里不需要再次关闭，避免重复关闭
    }

}
