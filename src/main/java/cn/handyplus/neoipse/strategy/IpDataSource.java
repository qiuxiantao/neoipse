package cn.handyplus.neoipse.strategy;

import java.util.function.Consumer;

/**
 * IP数据源接口
 *
 * @author 滔天
 */
public interface IpDataSource {

    /**
     * 同步获取地域信息
     * 注意：此方法会在调用线程中执行，可能会阻塞
     *
     * @param ip IP
     * @return 地域信息
     */
    String getRegion(String ip);

    /**
     * 异步获取地域信息
     * 注意：此方法会在后台线程中执行，不会阻塞主线程
     *
     * @param ip IP
     * @param callback 回调函数，参数为地域信息，为null表示查询失败
     */
    void getRegionAsync(String ip, Consumer<String> callback);

}