package cn.handyplus.neoipse.strategy;

import cn.handyplus.neoipse.util.DataSourceManager;

import java.util.function.Consumer;

/**
 * IP数据源抽象基类
 * 提供公共功能和数据源集成
 *
 * @author 滔天
 */
public abstract class AbstractIpDataSource implements IpDataSource {

    /**
     * 数据源管理器（懒加载）
     */
    private DataSourceManager dataSourceManager;

    /**
     * 获取数据源管理器
     * 懒加载，避免初始化顺序问题
     *
     * @return 数据源管理器
     */
    protected DataSourceManager getDataSourceManager() {
        if (dataSourceManager == null) {
            dataSourceManager = DataSourceManager.getInstance();
        }
        return dataSourceManager;
    }

    /**
     * 获取数据源名称
     * 由子类实现
     *
     * @return 数据源名称
     */
    protected abstract String getDataSourceName();

    /**
     * 记录查询结果
     *
     * @param success 是否成功
     */
    protected void recordQueryResult(boolean success) {
        try {
            DataSourceManager manager = getDataSourceManager();
            if (manager != null) {
                manager.recordQueryResult(getDataSourceName(), success);
            }
        } catch (Exception e) {
            // 记录失败不影响主功能，但应该记录日志以便排查
            cn.handyplus.lib.util.MessageUtil.sendConsoleMessage(
                "Failed to record query result for " + getDataSourceName() + ", error: " + e.getMessage()
            );
        }
    }

    @Override
    public String getRegion(String ip) {
        try {
            String region = doGetRegion(ip);
            recordQueryResult(region != null && !region.isEmpty());
            return region;
        } catch (Exception e) {
            recordQueryResult(false);
            throw e;
        }
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        try {
            doGetRegionAsync(ip, region -> {
                recordQueryResult(region != null && !region.isEmpty());
                callback.accept(region);
            });
        } catch (Exception e) {
            recordQueryResult(false);
            callback.accept(null);
        }
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

