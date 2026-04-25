package cn.handyplus.neoipse.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 异步任务工具类
 * 负责管理异步任务的线程池
 *
 * @author 滔天
 */
public class AsyncUtil {

    /**
     * 单例实例
     */
    private static final AsyncUtil INSTANCE = new AsyncUtil();

    /**
     * 线程池
     */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 私有构造方法
     */
    private AsyncUtil() {
    }

    /**
     * 获取单例实例
     *
     * @return 异步任务工具实例
     */
    public static AsyncUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 执行异步任务
     *
     * @param task 任务
     */
    public void execute(Runnable task) {
        executorService.submit(task);
    }

    /**
     * 执行带有回调的异步任务
     *
     * @param task 任务
     * @param callback 回调
     * @param <T> 任务返回类型
     */
    public <T> void executeWithCallback(Task<T> task, Consumer<T> callback) {
        executorService.submit(() -> {
            T result = task.execute();
            callback.accept(result);
        });
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
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

    /**
     * 任务接口
     *
     * @param <T> 任务返回类型
     */
    @FunctionalInterface
    public interface Task<T> {
        T execute();
    }

}
