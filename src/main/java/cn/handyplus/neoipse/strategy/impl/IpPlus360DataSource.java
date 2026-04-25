package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.http.HttpManager;
import cn.handyplus.neoipse.strategy.AbstractIpDataSource;
import cn.handyplus.neoipse.util.ConfigCacheUtil;
import cn.handyplus.neoipse.util.ExceptionUtil;
import cn.handyplus.neoipse.util.RegionUtil;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * IpPlus360数据源
 *
 * @author 滔天
 */
public class IpPlus360DataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IPPLUS360";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String ipv4Key = ConfigCacheUtil.getString("ipPlus360Ipv4Key", "");
            String ipv6Key = ConfigCacheUtil.getString("ipPlus360Ipv6Key", "");

            // 验证API密钥
            ipv4Key = validationManager.sanitizeApiKey(ipv4Key);
            if (ipv4Key == null) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 请配置有效的IpPlus360密钥!");
                return null;
            }

            boolean isIpv6 = ip.contains(":");
            String key = isIpv6 && !ipv6Key.isEmpty() ? validationManager.sanitizeApiKey(ipv6Key) : ipv4Key;
            if (key == null) {
                key = ipv4Key;
            }

            String apiUrl = isIpv6 ?
                "https://api.ip360.net.cn/geo/ipv6?key=" + key + "&ip=" + ip :
                "https://api.ip360.net.cn/geo/ipv4?key=" + key + "&ip=" + ip;

            String response = httpManager.get(apiUrl);

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

            String country = RegionUtil.getJsonString(data, "country");
            String province = RegionUtil.getJsonString(data, "province");
            String city = RegionUtil.getJsonString(data, "city");
            String isp = RegionUtil.getJsonString(data, "isp");

            return country + "|" + province + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpPlus360DataSource.getRegion", e, null);
        }
    }

    @Override
    protected void doGetRegionAsync(String ip, Consumer<String> callback) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                callback.accept(null);
                return;
            }

            String ipv4Key = ConfigCacheUtil.getString("ipPlus360Ipv4Key", "");
            String ipv6Key = ConfigCacheUtil.getString("ipPlus360Ipv6Key", "");

            // 验证API密钥
            ipv4Key = validationManager.sanitizeApiKey(ipv4Key);
            if (ipv4Key == null) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 请配置有效的IpPlus360密钥!");
                callback.accept(null);
                return;
            }

            boolean isIpv6 = ip.contains(":");
            String selectedKey = isIpv6 && !ipv6Key.isEmpty() ? validationManager.sanitizeApiKey(ipv6Key) : ipv4Key;
            if (selectedKey == null) {
                selectedKey = ipv4Key;
            }

            final String key = selectedKey;
            String apiUrl = isIpv6 ?
                "https://api.ip360.net.cn/geo/ipv6?key=" + key + "&ip=" + ip :
                "https://api.ip360.net.cn/geo/ipv4?key=" + key + "&ip=" + ip;

            httpManager.getAsync(apiUrl, response -> {
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

                    String country = RegionUtil.getJsonString(data, "country");
                    String province = RegionUtil.getJsonString(data, "province");
                    String city = RegionUtil.getJsonString(data, "city");
                    String isp = RegionUtil.getJsonString(data, "isp");

                    callback.accept(country + "|" + province + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText());
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

}

