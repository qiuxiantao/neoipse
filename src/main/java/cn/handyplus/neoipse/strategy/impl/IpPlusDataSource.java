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
 * IPPlus数据源
 * API文档: https://ip.plus/docs
 *
 * @author 滔天
 */
public class IpPlusDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IPPLUS";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String url = "https://api.ip.plus/" + ip;
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            int code = json.optInt("code");
            if (code != 200) {
                String message = json.optString("message");
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IPPlus API错误: " + message);
                return null;
            }

            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                return null;
            }

            String country = RegionUtil.getJsonString(data, "country");
            String subdivisions = RegionUtil.getJsonString(data, "subdivisions");
            String city = RegionUtil.getJsonString(data, "city");
            String asName = RegionUtil.getJsonString(data, "as_name");

            return country + "|" + subdivisions + "|" + city + "|" + asName + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpPlusDataSource.getRegion", e, null);
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

        httpManager.getAsync("https://api.ip.plus/" + sanitizedIp, response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                int code = json.optInt("code");
                if (code != 200) {
                    String message = json.optString("message");
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] IPPlus API错误: " + message);
                    callback.accept(null);
                    return;
                }

                JSONObject data = json.optJSONObject("data");
                if (data == null) {
                    callback.accept(null);
                    return;
                }

                String country = RegionUtil.getJsonString(data, "country");
                String subdivisions = RegionUtil.getJsonString(data, "subdivisions");
                String city = RegionUtil.getJsonString(data, "city");
                String asName = RegionUtil.getJsonString(data, "as_name");

                callback.accept(country + "|" + subdivisions + "|" + city + "|" + asName + "|" + RegionUtil.getUnknownText());
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("IpPlusDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

}

