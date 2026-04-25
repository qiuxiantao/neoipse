package cn.handyplus.neoipse.test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * WHOIS API 测试工具
 */
public class WhoisApiTest {

    public static void main(String[] args) {
        try {
            // 测试IP
            String testIp = "8.8.8.8";
            
            System.out.println("=== 测试 whois.pconline.com.cn API ===");
            System.out.println("测试IP: " + testIp);
            System.out.println();
            
            URL url = new URL("https://whois.pconline.com.cn/ipJson.jsp?ip=" + testIp + "&json=true");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept-Charset", "GBK");

            System.out.println("响应码: " + conn.getResponseCode());
            System.out.println();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseStr = response.toString();
            System.out.println("原始响应: " + responseStr);
            System.out.println();

            // 解析JSON
            JSONObject json = new JSONObject(responseStr);
            
            System.out.println("=== JSON 字段解析 ===");
            System.out.println("country: " + json.optString("country"));
            System.out.println("region: " + json.optString("region"));
            System.out.println("city: " + json.optString("city"));
            System.out.println("isp: " + json.optString("isp"));
            System.out.println();

            // 测试本地IP
            System.out.println("=== 测试本地IP 127.0.0.1 ===");
            testIp("127.0.0.1");
            
            // 测试内网IP
            System.out.println("=== 测试内网IP 192.168.1.1 ===");
            testIp("192.168.1.1");

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testIp(String ip) {
        try {
            URL url = new URL("https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Accept-Charset", "GBK");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("IP: " + ip + " -> " + response.toString());
        } catch (Exception e) {
            System.out.println("IP: " + ip + " -> 错误: " + e.getMessage());
        }
    }
}
