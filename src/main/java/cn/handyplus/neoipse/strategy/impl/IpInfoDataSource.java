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
 * IPInfo数据源
 *
 * @author 滔天
 */
public class IpInfoDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "IPINFO";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String url = "https://ipinfo.io/json/" + ip;
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            String country = RegionUtil.getJsonString(json, "country");
            String region = RegionUtil.getJsonString(json, "region");
            String city = RegionUtil.getJsonString(json, "city");
            String org = RegionUtil.getJsonString(json, "org");

            // 国家代码转换
            if (!RegionUtil.isUnknownValue(country) && country.length() == 2) {
                country = convertCountryCode(country);
            }

            return country + "|" + region + "|" + city + "|" + org + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpInfoDataSource.getRegion", e, null);
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

        String url = "https://ipinfo.io/json/" + sanitizedIp;
        httpManager.getAsync(url, response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                String country = RegionUtil.getJsonString(json, "country");
                String region = RegionUtil.getJsonString(json, "region");
                String city = RegionUtil.getJsonString(json, "city");
                String org = RegionUtil.getJsonString(json, "org");

                // 国家代码转换
                if (!RegionUtil.isUnknownValue(country) && country.length() == 2) {
                    country = convertCountryCode(country);
                }

                callback.accept(country + "|" + region + "|" + city + "|" + org + "|" + RegionUtil.getUnknownText());
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("IpInfoDataSource.getRegionAsync", e);
                callback.accept(null);
            }
        });
    }

    /**
     * 转换国家代码为中文名称
     *
     * @param countryCode 国家代码
     * @return 中文国家名称
     */
    private String convertCountryCode(String countryCode) {
        // 简单的国家代码映射
        switch (countryCode) {
            case "CN": return "中国";
            case "US": return "美国";
            case "JP": return "日本";
            case "KR": return "韩国";
            case "GB": return "英国";
            case "DE": return "德国";
            case "FR": return "法国";
            case "RU": return "俄罗斯";
            case "IN": return "印度";
            case "AU": return "澳大利亚";
            case "CA": return "加拿大";
            case "BR": return "巴西";
            case "MX": return "墨西哥";
            case "SG": return "新加坡";
            case "HK": return "香港";
            case "TW": return "台湾";
            default: return countryCode;
        }
    }

}

