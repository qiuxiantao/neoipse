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
 * IPQuery数据源
 *
 * @author 滔天
 */
public class IpQueryDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IPQUERY";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String url = "https://ipquery.io/id/" + ip;
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            String country = RegionUtil.getJsonString(json, "country");
            String state = RegionUtil.getJsonString(json, "state");
            String city = RegionUtil.getJsonString(json, "city");
            String isp = RegionUtil.getJsonString(json, "isp");

            return country + "|" + state + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return cn.handyplus.neoipse.util.ExceptionUtil.getInstance().handleException("IpQueryDataSource.getRegion", e, null);
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

        String url = "https://ipquery.io/id/" + sanitizedIp;
        httpManager.getAsync(url, response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                String country = RegionUtil.getJsonString(json, "country");
                String state = RegionUtil.getJsonString(json, "state");
                String city = RegionUtil.getJsonString(json, "city");
                String isp = RegionUtil.getJsonString(json, "isp");

                callback.accept(country + "|" + state + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText());
            } catch (Exception e) {
                cn.handyplus.neoipse.util.ExceptionUtil.getInstance().handleException("IpQueryDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

}

