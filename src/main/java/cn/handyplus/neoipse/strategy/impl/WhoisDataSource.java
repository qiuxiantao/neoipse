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
 * WHOIS数据源
 *
 * @author 滔天
 */
public class WhoisDataSource extends AbstractIpDataSource {

    private final HttpManager httpManager = HttpManager.getInstance();
    private final ValidationManager validationManager = ValidationManager.getInstance();

    @Override
    protected String getDataSourceName() {
        return "WHOIS";
    }

    @Override
    protected String doGetRegion(String ip) {
        try {
            // 验证并清理IP地址
            ip = validationManager.sanitizeIp(ip);
            if (ip == null) {
                return null;
            }

            String url = "https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
            String responseStr = httpManager.get(url);
            if (responseStr == null) {
                return null;
            }

            // 使用JSONObject解析
            JSONObject json = new JSONObject(responseStr);

            // 检查是否有真正的错误（排除 noprovince 等提示信息）
            String errMsg = json.optString("err");
            if (!errMsg.isEmpty() && !"success".equalsIgnoreCase(errMsg) && !"noprovince".equalsIgnoreCase(errMsg)) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS API提示: " + errMsg);
            }

            String country = RegionUtil.getJsonString(json, "country");
            String region = RegionUtil.getJsonString(json, "region");
            String city = RegionUtil.getJsonString(json, "city");
            String isp = RegionUtil.getJsonString(json, "isp");
            String addr = RegionUtil.getJsonString(json, "addr");

            // 如果省份为空但有addr，尝试从addr中提取国家信息
            if (!RegionUtil.isUnknownValue(country) && !RegionUtil.isUnknownValue(addr)) {
                // country 已经有值，不需要处理
            } else if (RegionUtil.isUnknownValue(country) && !RegionUtil.isUnknownValue(addr)) {
                country = addr.replace(" ", "");
            }

            // 如果省份为空但addr有值，优先使用addr中的地区信息
            if (RegionUtil.isUnknownValue(region) && !RegionUtil.isUnknownValue(addr)) {
                region = addr;
            }

            return country + "|" + region + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS查询失败: " + e.getMessage());
            return null;
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

        String url = "https://whois.pconline.com.cn/ipJson.jsp?ip=" + sanitizedIp + "&json=true";
        httpManager.getAsync(url, responseStr -> {
            try {
                if (responseStr == null) {
                    callback.accept(null);
                    return;
                }

                // 使用JSONObject解析
                JSONObject json = new JSONObject(responseStr);

                // 检查是否有真正的错误（排除 noprovince 等提示信息）
                String errMsg = json.optString("err");
                if (!errMsg.isEmpty() && !"success".equalsIgnoreCase(errMsg) && !"noprovince".equalsIgnoreCase(errMsg)) {
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS API提示: " + errMsg);
                }

                String country = RegionUtil.getJsonString(json, "country");
                String region = RegionUtil.getJsonString(json, "region");
                String city = RegionUtil.getJsonString(json, "city");
                String isp = RegionUtil.getJsonString(json, "isp");
                String addr = RegionUtil.getJsonString(json, "addr");

                // 如果省份为空但有addr，尝试从addr中提取国家信息
                if (!RegionUtil.isUnknownValue(country) && !RegionUtil.isUnknownValue(addr)) {
                    // country 已经有值，不需要处理
                } else if (RegionUtil.isUnknownValue(country) && !RegionUtil.isUnknownValue(addr)) {
                    country = addr.replace(" ", "");
                }

                // 如果省份为空但addr有值，优先使用addr中的地区信息
                if (RegionUtil.isUnknownValue(region) && !RegionUtil.isUnknownValue(addr)) {
                    region = addr;
                }

                String result = country + "|" + region + "|" + city + "|" + isp + "|" + RegionUtil.getUnknownText();
                callback.accept(result);
            } catch (Exception e) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS异步查询失败: " + e.getMessage());
                callback.accept(null);
            }
        });
    }

}

