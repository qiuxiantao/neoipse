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
 * IP9数据源
 * API文档: https://www.ip9.com.cn/
 *
 * @author 滔天
 */
public class Ip9DataSource implements IpDataSource {

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
            
            // IP9 API格式: https://ip9.com.cn/get?ip=58.30.0.0
            String url = "https://ip9.com.cn/get?ip=" + ip;
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            // IP9返回ret字段，不是code
            int ret = json.optInt("ret");
            if (ret != 200) {
                String message = json.optString("msg", "未知错误");
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IP9 API错误: " + message);
                return null;
            }

            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                return null;
            }

            String country = getJsonString(data, "country");
            String province = getJsonString(data, "prov");
            String city = getJsonString(data, "city");
            String isp = getJsonString(data, "isp");

            return country + "|" + province + "|" + city + "|" + isp + "|未知";
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("Ip9DataSource.getRegion", e, null);
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
        
        String url = "https://ip9.com.cn/get?ip=" + ip;
        httpManager.getAsync(url, response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                // IP9返回ret字段，不是code
                int ret = json.optInt("ret");
                if (ret != 200) {
                    String message = json.optString("msg", "未知错误");
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IP9 API错误: " + message);
                    callback.accept(null);
                    return;
                }

                JSONObject data = json.optJSONObject("data");
                if (data == null) {
                    callback.accept(null);
                    return;
                }

                String country = getJsonString(data, "country");
                String province = getJsonString(data, "prov");
                String city = getJsonString(data, "city");
                String isp = getJsonString(data, "isp");

                callback.accept(country + "|" + province + "|" + city + "|" + isp + "|未知");
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("Ip9DataSource.getRegionAsync", e);
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
