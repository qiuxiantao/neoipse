package cn.handyplus.neoipse.strategy.impl;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.strategy.IpDataSource;
import cn.handyplus.neoipse.http.HttpManager;
import cn.handyplus.neoipse.validation.ValidationManager;
import org.bukkit.ChatColor;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * WHOIS数据源
 *
 * @author 滔天
 */
public class WhoisDataSource implements IpDataSource {

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
            
            String url = "https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
            String responseStr = httpManager.get(url);
            if (responseStr == null) {
                return null;
            }

            // 使用JSONObject解析
            JSONObject json = new JSONObject(responseStr);

            // 检查是否有错误
            String errMsg = json.optString("err");
            if (!errMsg.isEmpty() && !"success".equalsIgnoreCase(errMsg)) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS API错误: " + errMsg);
            }

            String country = getJsonString(json, "country");
            String region = getJsonString(json, "region");
            String city = getJsonString(json, "city");
            String isp = getJsonString(json, "isp");
            String addr = getJsonString(json, "addr");

            // 如果省份为空但有addr，尝试从addr中提取国家信息
            if ("未知".equals(country) && !addr.isEmpty() && !"未知".equals(addr)) {
                country = addr.replace(" ", "");
            }

            // 如果省份为空但addr有值，优先使用addr中的地区信息
            if ("未知".equals(region) && !addr.isEmpty() && !"未知".equals(addr)) {
                region = addr;
            }

            // 如果城市为空且省份也不是"未知"，城市设为"未知"
            if ("未知".equals(city) && !"未知".equals(region)) {
                // 城市保持"未知"
            }

            return country + "|" + region + "|" + city + "|" + isp + "|未知";
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS查询失败: " + e.getMessage());
            return null;
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
        
        String url = "https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
        httpManager.getAsync(url, responseStr -> {
            try {
                if (responseStr == null) {
                    callback.accept(null);
                    return;
                }
                
                // 使用JSONObject解析
                JSONObject json = new JSONObject(responseStr);

                // 检查是否有错误
                String errMsg = json.optString("err");
                if (!errMsg.isEmpty() && !"success".equalsIgnoreCase(errMsg)) {
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS API错误: " + errMsg);
                }

                String country = getJsonString(json, "country");
                String region = getJsonString(json, "region");
                String city = getJsonString(json, "city");
                String isp = getJsonString(json, "isp");
                String addr = getJsonString(json, "addr");

                // 如果省份为空但有addr，尝试从addr中提取国家信息
                if ("未知".equals(country) && !addr.isEmpty() && !"未知".equals(addr)) {
                    country = addr.replace(" ", "");
                }

                // 如果省份为空但addr有值，优先使用addr中的地区信息
                if ("未知".equals(region) && !addr.isEmpty() && !"未知".equals(addr)) {
                    region = addr;
                }

                String result = country + "|" + region + "|" + city + "|" + isp + "|未知";
                callback.accept(result);
            } catch (Exception e) {
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] WHOIS异步查询失败: " + e.getMessage());
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
                if (value != null && !value.isEmpty() && !"null".equalsIgnoreCase(value)) {
                    return value.trim();
                }
            }
        } catch (Exception e) {
            // 忽略解析异常
        }
        return "未知";
    }

}