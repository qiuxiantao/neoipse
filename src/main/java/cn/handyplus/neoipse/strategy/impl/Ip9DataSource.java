package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.http.HttpManager;
import cn.handyplus.neoipse.strategy.AbstractIpDataSource;
import cn.handyplus.neoipse.util.RegionUtil;
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
public class Ip9DataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IP9";
    }

    @Override
    protected String doGetRegion(String ip) {
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

            String country = RegionUtil.getJsonString(data, "country");
            String province = RegionUtil.getJsonString(data, "prov");
            String city = RegionUtil.getJsonString(data, "city");
            String isp = RegionUtil.getJsonString(data, "isp");

            return country + "|" + province + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return cn.handyplus.neoipse.util.ExceptionUtil.getInstance().handleException("Ip9DataSource.getRegion", e, null);
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

        String url = "https://ip9.com.cn/get?ip=" + sanitizedIp;
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

                String country = RegionUtil.getJsonString(data, "country");
                String province = RegionUtil.getJsonString(data, "prov");
                String city = RegionUtil.getJsonString(data, "city");
                String isp = RegionUtil.getJsonString(data, "isp");

                callback.accept(country + "|" + province + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText());
            } catch (Exception e) {
                cn.handyplus.neoipse.util.ExceptionUtil.getInstance().handleException("Ip9DataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

}

