package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.util.ExceptionUtil;
import cn.handyplus.neoipse.http.HttpManager;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * IPAPI数据源
 *
 * @author 滔天
 */
public class IpApiDataSource implements IpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    public String getRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }
            
            String url = "https://ip-api.com/json/" + ip + "?lang=zh-CN";
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            if (!"success".equals(json.optString("status"))) {
                String message = json.optString("message", "未知错误");
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IPAPI错误: " + message);
                return null;
            }

            String country = getJsonString(json, "country");
            String regionName = getJsonString(json, "regionName");
            String city = getJsonString(json, "city");
            String isp = getJsonString(json, "isp");

            return country + "|" + regionName + "|" + city + "|" + isp + "|未知";
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpApiDataSource.getRegion", e, null);
        }
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        // 验证并清理IP地址
        ip = validationManager.sanitizeIp(ip);
        if (ip == null) {
            callback.accept(null);
            return;
        }
        
        httpManager.getAsync("https://ip-api.com/json/" + ip + "?lang=zh-CN", response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                if (!"success".equals(json.optString("status"))) {
                    String message = json.optString("message", "未知错误");
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IPAPI错误: " + message);
                    callback.accept(null);
                    return;
                }

                String country = getJsonString(json, "country");
                String regionName = getJsonString(json, "regionName");
                String city = getJsonString(json, "city");
                String isp = getJsonString(json, "isp");

                callback.accept(country + "|" + regionName + "|" + city + "|" + isp + "|未知");
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("IpApiDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
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