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
 * VoreApi数据源
 *
 * @author 滔天
 */
public class VoreApiDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "VOREAPI";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String url = "https://api.vore.top/ip?ip=" + ip;
            String response = httpManager.get(url);

            if (response == null) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi查询失败: api.vore.top");
                return null;
            }

            JSONObject json = new JSONObject(response);

            int code = json.optInt("code");
            if (code != 200) {
                String message = json.optString("msg");
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi错误: " + message);
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
            return ExceptionUtil.getInstance().handleException("VoreApiDataSource.getRegion", e, null);
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

        String url = "https://api.vore.top/ip?ip=" + sanitizedIp;
        httpManager.getAsync(url, response -> {
            try {
                if (response == null) {
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi查询失败: api.vore.top");
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                int code = json.optInt("code");
                if (code != 200) {
                    String message = json.optString("msg");
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] VoreApi错误: " + message);
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
                ExceptionUtil.getInstance().handleException("VoreApiDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

}

