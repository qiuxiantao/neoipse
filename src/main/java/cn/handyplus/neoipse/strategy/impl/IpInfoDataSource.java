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
 * IPinfo Lite数据源
 * API文档: https://ipinfo.io/developers
 *
 * @author 滔天
 */
public class IpInfoDataSource implements IpDataSource {

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
            
            // IPInfo API格式: https://ipinfo.io/{ip}/json
            String url = "https://ipinfo.io/" + ip + "/json";
            String response = httpManager.get(url);

            if (response == null) {
                return null;
            }

            JSONObject json = new JSONObject(response);

            String country = getJsonString(json, "country");
            String region = getJsonString(json, "region");
            String city = getJsonString(json, "city");
            String org = getJsonString(json, "org");

            // IPinfo返回的是国家代码，需要转换为国家名称
            if (!"未知".equals(country) && country.length() == 2) {
                country = getCountryName(country);
            }

            return country + "|" + region + "|" + city + "|" + org + "|未知";
        } catch (Exception e) {
            return ExceptionUtil.getInstance().handleException("IpInfoDataSource.getRegion", e, null);
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
        
        httpManager.getAsync("https://ipinfo.io/" + ip + "/json", response -> {
            try {
                if (response == null) {
                    callback.accept(null);
                    return;
                }

                JSONObject json = new JSONObject(response);

                String country = getJsonString(json, "country");
                String region = getJsonString(json, "region");
                String city = getJsonString(json, "city");
                String org = getJsonString(json, "org");

                // IPinfo返回的是国家代码，需要转换为国家名称
                if (!"未知".equals(country) && country.length() == 2) {
                    country = getCountryName(country);
                }

                callback.accept(country + "|" + region + "|" + city + "|" + org + "|未知");
            } catch (Exception e) {
                ExceptionUtil.getInstance().handleException("IpInfoDataSource.getRegionAsync", e);
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

    /**
     * 将国家代码转换为国家名称
     *
     * @param countryCode 国家代码
     * @return 国家名称
     */
    private String getCountryName(String countryCode) {
        switch (countryCode) {
            case "CN":
                return "中国";
            case "TW":
                return "台湾";
            case "US":
                return "美国";
            case "JP":
                return "日本";
            case "KR":
                return "韩国";
            case "GB":
                return "英国";
            case "CA":
                return "加拿大";
            case "AU":
                return "澳大利亚";
            case "DE":
                return "德国";
            case "FR":
                return "法国";
            case "RU":
                return "俄罗斯";
            case "ES":
                return "西班牙";
            case "IT":
                return "意大利";
            case "IN":
                return "印度";
            case "BR":
                return "巴西";
            case "MX":
                return "墨西哥";
            case "SG":
                return "新加坡";
            case "HK":
                return "香港";
            case "TH":
                return "泰国";
            case "VN":
                return "越南";
            case "ID":
                return "印度尼西亚";
            case "MY":
                return "马来西亚";
            case "PH":
                return "菲律宾";
            case "NL":
                return "荷兰";
            case "SE":
                return "瑞典";
            case "NO":
                return "挪威";
            case "DK":
                return "丹麦";
            case "FI":
                return "芬兰";
            case "PL":
                return "波兰";
            case "CZ":
                return "捷克";
            case "AT":
                return "奥地利";
            case "CH":
                return "瑞士";
            case "IE":
                return "爱尔兰";
            case "NZ":
                return "新西兰";
            default:
                return countryCode;
        }
    }

}
