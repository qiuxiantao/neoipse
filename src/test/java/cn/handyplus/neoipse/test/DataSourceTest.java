package cn.handyplus.neoipse.test;

import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.strategy.impl.IpApiDataSource;
import cn.handyplus.neoipse.strategy.impl.VoreApiDataSource;
import cn.handyplus.neoipse.strategy.impl.WhoisDataSource;

/**
 * 数据源测试工具
 */
public class DataSourceTest {

    public static void main(String[] args) {
        // 测试IP
        String testIp = "8.8.8.8";
        
        System.out.println("=== IP数据源测试 ===");
        System.out.println("测试IP: " + testIp);
        System.out.println();

        // 测试WHOIS
        System.out.println("--- WHOIS (whois.pconline.com.cn) ---");
        testDataSource(new WhoisDataSource(), testIp);
        System.out.println();

        // 测试IPAPI
        System.out.println("--- IPAPI (ip-api.com) ---");
        testDataSource(new IpApiDataSource(), testIp);
        System.out.println();

        // 测试VoreAPI
        System.out.println("--- VoreAPI (api.vore.top) ---");
        testDataSource(new VoreApiDataSource(), testIp);
        System.out.println();

        // 测试中国IP
        String chinaIp = "14.215.116.1"; // 京东的IP
        System.out.println("=== 中国IP测试 ===");
        System.out.println("测试IP: " + chinaIp);
        System.out.println();

        System.out.println("--- WHOIS ---");
        testDataSource(new WhoisDataSource(), chinaIp);
        System.out.println();

        System.out.println("--- IPAPI ---");
        testDataSource(new IpApiDataSource(), chinaIp);
        System.out.println();

        System.out.println("--- VoreAPI ---");
        testDataSource(new VoreApiDataSource(), chinaIp);
    }

    private static void testDataSource(IpDataSource dataSource, String ip) {
        try {
            System.out.println("同步查询结果: " + dataSource.getRegion(ip));
        } catch (Exception e) {
            System.out.println("查询失败: " + e.getMessage());
        }
    }
}
