package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.util.DataSourceManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 多数据源自动切换实现
 * 当主数据源失败时，自动尝试备用的数据源
 *
 * @author 滔天
 */
public class FallbackIpDataSource implements IpDataSource {

    private final DataSourceManager dataSourceManager = DataSourceManager.getInstance();

    @Override
    public String getRegion(String ip) {
        List<String> triedDataSources = new ArrayList<>();

        // 获取按权重排序的数据源
        List<DataSourceManager.DataSourceInfo> sortedDataSources = dataSourceManager.getSortedDataSources();

        for (DataSourceManager.DataSourceInfo info : sortedDataSources) {
            String dataSourceName = info.getName();
            IpDataSource dataSource = info.getSource();
            triedDataSources.add(dataSourceName);

            try {
                String region = dataSource.getRegion(ip);
                if (region != null && !region.isEmpty()) {
                    if (triedDataSources.size() > 1) {
                        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] " + dataSourceName + " 数据源成功返回结果");
                    }
                    return region;
                }
            } catch (Exception e) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] " + dataSourceName + " 数据源查询失败: " + e.getMessage());
            }
        }

        MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 所有数据源均查询失败，已尝试: " + triedDataSources);
        return null;
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        // 使用AsyncUtil的线程池处理异步请求
        cn.handyplus.neoipse.util.AsyncUtil.getInstance().executeWithCallback(
            () -> getRegion(ip),
            callback
        );
    }

}