package cn.handyplus.neoipse.util;

import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.strategy.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据源管理类
 * 负责管理所有数据源，提供按权重排序的数据源列表
 *
 * @author 滔天
 */
public class DataSourceManager {

    /**
     * 单例实例
     */
    private static final DataSourceManager INSTANCE = new DataSourceManager();

    /**
     * 数据源列表
     */
    private final List<DataSourceInfo> dataSources = new ArrayList<>();

    /**
     * 私有构造方法
     */
    private DataSourceManager() {
        initDataSources();
    }

    /**
     * 获取单例实例
     *
     * @return 数据源管理实例
     */
    public static DataSourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化数据源
     */
    private void initDataSources() {
        // 按默认权重添加数据源（权重越高越优先）
        // 注意：IP9和VOREAPI可能无法访问，优先使用其他数据源
        addDataSource("IPPLUS", new IpPlusDataSource(), 100);
        addDataSource("WHOIS", new WhoisDataSource(), 85);
        addDataSource("IPINFO", new IpInfoDataSource(), 80);
        addDataSource("IPQUERY", new IpQueryDataSource(), 75);
        addDataSource("IPPLUS360", new IpPlus360DataSource(), 70);
        addDataSource("IPAPI", new IpApiDataSource(), 65);
        addDataSource("IP9", new Ip9DataSource(), 30);
        addDataSource("VOREAPI", new VoreApiDataSource(), 20);
    }

    /**
     * 添加数据源
     *
     * @param name   数据源名称
     * @param source 数据源实例
     * @param weight 权重
     */
    public void addDataSource(String name, IpDataSource source, int weight) {
        dataSources.add(new DataSourceInfo(name, source, weight));
    }

    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    public List<DataSourceInfo> getDataSources() {
        return dataSources;
    }

    /**
     * 根据权重获取排序后的数据源
     *
     * @return 排序后的数据源列表
     */
    public List<DataSourceInfo> getSortedDataSources() {
        List<DataSourceInfo> sorted = new ArrayList<>(dataSources);
        // 按权重从高到低排序
        sorted.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
        return sorted;
    }

    /**
     * 根据名称获取数据源实例
     *
     * @param dataSourceName 数据源名称（不区分大小写）
     * @return 数据源实例，如果没有找到则返回 fallback
     */
    public IpDataSource getDataSourceByName(String dataSourceName) {
        // 特殊处理 fallback
        if ("fallback".equalsIgnoreCase(dataSourceName)) {
            return new cn.handyplus.neoipse.strategy.impl.FallbackIpDataSource();
        }

        // 从现有数据源中查找
        for (DataSourceInfo info : dataSources) {
            if (info.getName().equalsIgnoreCase(dataSourceName)) {
                return info.getSource();
            }
        }

        // 默认返回 fallback
        return new cn.handyplus.neoipse.strategy.impl.FallbackIpDataSource();
    }

    /**
     * 获取可用的数据源名称列表
     *
     * @return 数据源名称列表（包含 fallback）
     */
    public List<String> getAvailableDataSourceNames() {
        List<String> names = new ArrayList<>();
        names.add("fallback");
        for (DataSourceInfo info : dataSources) {
            names.add(info.getName().toLowerCase());
        }
        return names;
    }

    /**
     * 数据源信息类
     */
    public static class DataSourceInfo {
        private final String name;
        private final IpDataSource source;
        private final int weight;

        public DataSourceInfo(String name, IpDataSource source, int weight) {
            this.name = name;
            this.source = source;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public IpDataSource getSource() {
            return source;
        }

        public int getWeight() {
            return weight;
        }
    }

}
