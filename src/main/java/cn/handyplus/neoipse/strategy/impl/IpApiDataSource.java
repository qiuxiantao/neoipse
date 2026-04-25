package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.http.HttpManager;
import cn.handyplus.neoipse.strategy.AbstractIpDataSource;
import cn.handyplus.neoipse.util.ExceptionUtil;
import cn.handyplus.neoipse.util.RegionUtil;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * IPAPI数据源
 *
 * @author 滔天
 */
public class IpApiDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IPAPI";
    }

    @Override
    protected String doGetRegion(String ip) {
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

            String country = RegionUtil.getJsonString(json, "country");
            String regionName = RegionUtil.getJsonString(json, "regionName");
            String city = RegionUtil.getJsonString(json, "city");
            String isp = RegionUtil.getJsonString(json, "isp");

            return country + "|" + regionName + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpApiDataSource.getRegion", e, null);
        }
    }

    @Override
    protected void doGetRegionAsync(String ip, Consumer<String> callback) {
        // 验证并清理IP地址
        final String sanitizedIp = validationManager.sanitizeIp(ip);
        if (sanitizedIp == null) {
            callback.accept(null);
            return;
        }

        httpManager.getAsync("https://ip-api.com/json/" + sanitizedIp + "?lang=zh-CN", response -> {
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

                String country = RegionUtil.getJsonString(json, "country");
                String regionName = RegionUtil.getJsonString(json, "regionName");
                String city = RegionUtil.getJsonString(json, "city");
                String isp = RegionUtil.getJsonString(json, "isp");

                callback.accept(country + "|" + regionName + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText());
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("IpApiDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

}

