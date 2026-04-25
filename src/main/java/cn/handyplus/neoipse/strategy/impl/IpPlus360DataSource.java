package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.util.ConfigCacheUtil;
import cn.handyplus.neoipse.util.ExceptionUtil;
import cn.handyplus.neoipse.util.HttpUtil;
import cn.handyplus.neoipse.util.SecurityUtil;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * IpPlus360数据源
 *
 * @author 滔天
 */
public class IpPlus360DataSource implements IpDataSource {

    private final HttpUtil httpUtil = HttpUtil.getInstance();
    private final SecurityUtil securityUtil = SecurityUtil.getInstance();

    @Override
    public String getRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = securityUtil.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String ipv4Key = ConfigCacheUtil.getString("ipPlus360Ipv4Key", "");
            String ipv6Key = ConfigCacheUtil.getString("ipPlus360Ipv6Key", "");

            // 验证API密钥
            ipv4Key = securityUtil.sanitizeApiKey(ipv4Key);
            if (ipv4Key == null) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 请配置有效的IpPlus360密钥!");
                return null;
            }

            boolean isIpv6 = ip.contains(":");
            String key = isIpv6 && !ipv6Key.isEmpty() ? securityUtil.sanitizeApiKey(ipv6Key) : ipv4Key;
            if (key == null) {
                key = ipv4Key;
            }

            String apiUrl = isIpv6 ? 
                "https://api.ip360.net.cn/geo/ipv6?key=" + key + "&ip=" + ip :
                "https://api.ip360.net.cn/geo/ipv4?key=" + key + "&ip=" + ip;

            String response = httpUtil.get(apiUrl);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            if (json.optInt("code") != 0) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IpPlus360返回错误: " + json.optString("msg"));
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
            return ExceptionUtil.getInstance().handleException("IpPlus360DataSource.getRegion", e, null);
        }
    }

    @Override
    public void getRegionAsync(String ip, Consumer<String> callback) {
        try {
            // 验证并清理IP地址
            ip = securityUtil.sanitizeIp(ip);
            if (ip == null) {
                callback.accept(null);
                return;
            }

            String ipv4Key = ConfigCacheUtil.getString("ipPlus360Ipv4Key", "");
            String ipv6Key = ConfigCacheUtil.getString("ipPlus360Ipv6Key", "");

            // 验证API密钥
            ipv4Key = securityUtil.sanitizeApiKey(ipv4Key);
            if (ipv4Key == null) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 请配置有效的IpPlus360密钥!");
                callback.accept(null);
                return;
            }

            boolean isIpv6 = ip.contains(":");
            String key = isIpv6 && !ipv6Key.isEmpty() ? securityUtil.sanitizeApiKey(ipv6Key) : ipv4Key;
            if (key == null) {
                key = ipv4Key;
            }

            String apiUrl = isIpv6 ? 
                "https://api.ip360.net.cn/geo/ipv6?key=" + key + "&ip=" + ip :
                "https://api.ip360.net.cn/geo/ipv4?key=" + key + "&ip=" + ip;

            httpUtil.getAsync(apiUrl, response -> {
                try {
                    if (response == null) {
                        callback.accept(null);
                        return;
                    }

                    JSONObject json = new JSONObject(response);

                    if (json.optInt("code") != 0) {
                        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IpPlus360返回错误: " + json.optString("msg"));
                        callback.accept(null);
                        return;
                    }

                    JSONObject data = json.optJSONObject("data");
                    if (data == null) {
                        callback.accept(null);
                        return;
                    }

                    String country = getJsonString(data, "country");
                    String province = getJsonString(data, "province");
                    String city = getJsonString(data, "city");
                    String isp = getJsonString(data, "isp");

                    callback.accept(country + "|" + province + "|" + city + "|" + isp + "|未知");
                } catch (Exception e) {
                    ExceptionUtil.getInstance().handleException("IpPlus360DataSource.getRegionAsync", e);
                    callback.accept(null);
                }
            });
        } catch (Exception e) {
            ExceptionUtil.getInstance().handleException("IpPlus360DataSource.getRegionAsync", e);
            callback.accept(null);
        }
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