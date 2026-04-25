package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

/**
 * VoreApi数据源
 *
 * @author 滔天
 */
public class VoreApiDataSource implements IpDataSource {

    @Override
    public String getRegion(String ip) {
        try {
            URL url = new URL("https://api.vore.top/api/ip?ip=" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi请求失败: " + conn.getResponseCode());
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());

            if (json.optInt("code") != 200) {
                return null;
            }

            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                return null;
            }

            String country = getJsonString(data, "country");
            String province = getJsonString(data, "province");
            String city = getJsonString(data, "city");
            String isp = getJsonString(data, "isp");

            return country + "|" + province + "|" + city + "|" + isp + "|未知";
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi查询失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        new Thread(() -> {
            try {
                String result = getRegion(ip);
                callback.accept(result);
            } catch (Exception e) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi异步查询失败: " + e.getMessage());
                callback.accept(null);
            }
        }, "NeoIpSee-Async-Query").start();
    }

    /**
     * 安全获取JSON字符串值
     *
     * @param json JSON对象
     * @param key 键名
     * @return 字符串值，如果不存在或为空返回"未知"
     */
    private String getJsonString(JSONObject json, String key) {
        try {
            if (json.has(key)) {
                String value = json.getString(key);
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        } catch (Exception e) {
            // 忽略解析异常
        }
        return "未知";
    }

}