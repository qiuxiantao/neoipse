package cn.handyplus.neoipse.strategy;

import java.util.function.Consumer;

/**
 * IP数据源抽象基类
 * 提供公共功能
 *
 * @author 滔天
 */
public abstract class AbstractIpDataSource implements IpDataSource {

    /**
     * 获取数据源名称
     * 由子类实现
     *
     * @return 数据源名称
     */
    protected abstract String getDataSourceName();

    @Override
    public String getRegion(String ip) {
        return doGetRegion(ip);
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        doGetRegionAsync(ip, callback);
    }

    /**
     * 实际同步获取地域信息
     * 由子类实现
     *
     * @param ip IP
     * @return 地域信息
     */
    protected abstract String doGetRegion(String ip);

    /**
     * 实际异步获取地域信息
     * 由子类实现
     *
     * @param ip IP
     * @param callback 回调函数
     */
    protected abstract void doGetRegionAsync(String ip, Consumer<String> callback);

}
