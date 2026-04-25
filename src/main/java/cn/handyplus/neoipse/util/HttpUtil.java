package cn.handyplus.neoipse.util;

import cn.handyplus.lib.util.MessageUtil;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * HTTP工具类
 * 负责处理所有网络请求，包括线程池管理、超时控制和重试机制
 *
 * @author 滔天
 */
public class HttpUtil {

    /**
     * 单例实例
     */
    private static volatile HttpUtil INSTANCE;

    /**
     * HTTP客户端（使用Java 11+的HttpClient，内置连接池）
     */
    private final HttpClient httpClient;

    /**
     * 线程池
     */
    private final ExecutorService executorService;

    /**
     * 连接超时时间
     */
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

    /**
     * 读取超时时间
     */
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 2;

    /**
     * 重试间隔（毫秒）
     */
    private static final int RETRY_INTERVAL = 1000;

    /**
     * 私有构造方法
     */
    private HttpUtil() {
        this.executorService = Executors.newFixedThreadPool(10);
        // 创建带有连接池的HttpClient
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .executor(executorService)
                .build();
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
        return get(urlString, MAX_RETRY_COUNT);
    }

    /**
     * 发送GET请求（带重试）
     *
     * @param urlString URL字符串
     * @param retryCount 重试次数
     * @return 响应内容
     */
    private String get(String urlString, int retryCount) {
        // 提取API名称（从URL中提取域名作为API名称）
        String apiName = extractApiName(urlString);

        // 检查速率限制
        try {
            if (!RateLimiter.getInstance().allow(apiName)) {
                sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] API调用频率限制，等待后重试: " + apiName);
                Thread.sleep(1000); // 等待1秒后重试
            }
        } catch (Exception e) {
            // 忽略速率限制错误，继续执行
        }

        for (int i = 0; i <= retryCount; i++) {
            try {
                URI uri = new URI(urlString);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .timeout(READ_TIMEOUT)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                int responseCode = response.statusCode();
                if (responseCode != 200) {
                    throw new Exception("HTTP响应码: " + responseCode);
                }

                return response.body();
            } catch (URISyntaxException e) {
                sendConsoleMessage(ChatColor.RED + "[neoipSee] URL格式错误: " + urlString);
                sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误详情: " + e.getMessage());
                break; // URL格式错误，不需要重试
            } catch (IOException e) {
                if (i < retryCount) {
                    sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] HTTP请求失败(网络错误)，正在重试（" + (i + 1) + "/" + retryCount + "）: " + urlString);
                    sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 错误详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    sendConsoleMessage(ChatColor.RED + "[neoipSee] HTTP请求失败(网络错误)，已达到最大重试次数: " + urlString);
                    sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sendConsoleMessage(ChatColor.RED + "[neoipSee] HTTP请求被中断: " + urlString);
                break; // 线程中断，不需要重试
            } catch (Exception e) {
                if (i < retryCount) {
                    sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] HTTP请求失败，正在重试（" + (i + 1) + "/" + retryCount + "）: " + urlString);
                    sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 错误详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    sendConsoleMessage(ChatColor.RED + "[neoipSee] HTTP请求失败，已达到最大重试次数: " + urlString);
                    sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误详情: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * 异步发送GET请求
     *
     * @param urlString URL字符串
     * @param callback 回调函数
     */
    public void getAsync(String urlString, Consumer<String> callback) {
        executorService.submit(() -> {
            String result = get(urlString);
            callback.accept(result);
        });
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 从URL中提取API名称（域名）
     *
     * @param urlString URL字符串
     * @return API名称（域名）
     */
    private String extractApiName(String urlString) {
        try {
            URI uri = new URI(urlString);
            return uri.getHost();
        } catch (Exception e) {
            return "unknown"; // 如果解析失败，返回unknown
        }
    }

    /**
     * 安全发送控制台消息
     *
     * @param message 消息
     */
    private void sendConsoleMessage(String message) {
        try {
            MessageUtil.sendConsoleMessage(message);
        } catch (Exception e) {
            // 忽略消息发送错误
            System.out.println(message);
        }
    }

}
