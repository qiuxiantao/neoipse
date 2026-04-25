package cn.handyplus.neoipse.util;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.NeoIpSee;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件检查和修复工具类
 *
 * @author 滔天
 */
public class ConfigCheckUtil {

    private static final List<String> VALID_DATA_SOURCES = new ArrayList<>();
    private static final List<String> VALID_LANGUAGES = new ArrayList<>();

    static {
        VALID_DATA_SOURCES.add("fallback");
        VALID_DATA_SOURCES.add("ipplus");
        VALID_DATA_SOURCES.add("ipinfo");
        VALID_DATA_SOURCES.add("ip9");
        VALID_DATA_SOURCES.add("ipquery");
        VALID_DATA_SOURCES.add("ipplus360");
        VALID_DATA_SOURCES.add("ipapi");
        VALID_DATA_SOURCES.add("whois");
        VALID_DATA_SOURCES.add("voreapi");

        VALID_LANGUAGES.add("zh_CN");
        VALID_LANGUAGES.add("zh_TW");
        VALID_LANGUAGES.add("en_US");
        VALID_LANGUAGES.add("ja_JP");
        VALID_LANGUAGES.add("ko_KR");
        VALID_LANGUAGES.add("es_ES");
    }

    /**
     * 检查并修复配置文件
     */
    public static void checkAndFixConfig() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在检查配置文件...");

        File configFile = new File(NeoIpSee.INSTANCE.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 配置文件不存在，将重新生成默认配置");
            NeoIpSee.INSTANCE.saveDefaultConfig();
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        boolean needsSave = false;

        // 检查 language 配置
        String language = config.getString("language");
        if (language == null || !VALID_LANGUAGES.contains(language)) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 警告: language 配置无效 '" + language + "', 已自动修复为 zh_CN");
            config.set("language", "zh_CN");
            needsSave = true;
        }

        // 检查 dataSource 配置
        String dataSource = config.getString("dataSource");
        if (dataSource == null || !VALID_DATA_SOURCES.contains(dataSource.toLowerCase())) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 警告: dataSource 配置无效 '" + dataSource + "', 已自动修复为 fallback");
            config.set("dataSource", "fallback");
            needsSave = true;
        }

        // 检查必需的键是否存在
        if (!config.contains("removeProvinceAndCity")) {
            config.set("removeProvinceAndCity", false);
            needsSave = true;
        }

        if (!config.contains("unknown")) {
            config.set("unknown", "未知");
            needsSave = true;
        }

        if (!config.contains("local")) {
            config.set("local", "内网IP");
            needsSave = true;
        }

        if (!config.contains("isCheckUpdate")) {
            config.set("isCheckUpdate", true);
            needsSave = true;
        }

        if (!config.contains("isCheckUpdateToOpMsg")) {
            config.set("isCheckUpdateToOpMsg", true);
            needsSave = true;
        }

        if (!config.contains("ipPlus360Ipv4Key")) {
            config.set("ipPlus360Ipv4Key", "123456");
            needsSave = true;
        }

        if (!config.contains("ipPlus360Ipv6Key")) {
            config.set("ipPlus360Ipv6Key", "123456");
            needsSave = true;
        }

        // 保存修复后的配置
        if (needsSave) {
            try {
                config.save(configFile);
                MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 配置文件已自动修复并保存");
            } catch (IOException e) {
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 保存配置文件失败: " + e.getMessage());
            }
        } else {
            MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 配置文件检查通过");
        }
    }

    /**
     * 检查数据源是否可用
     *
     * @return 是否可用
     */
    public static boolean checkDataSource() {
        String dataSource = ConfigCacheUtil.getString("dataSource", "fallback").toLowerCase();

        if (!VALID_DATA_SOURCES.contains(dataSource)) {
            MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 错误: 无效的数据源 '" + dataSource + "'");
            return false;
        }

        // 检查特定数据源的配置
        switch (dataSource) {
            case "ipplus360":
                String ipv4Key = ConfigCacheUtil.getString("ipPlus360Ipv4Key", "");
                String ipv6Key = ConfigCacheUtil.getString("ipPlus360Ipv6Key", "");
                if (ipv4Key.isEmpty() || ipv4Key.equals("123456")) {
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: ipPlus360 模式需要配置有效的密钥，请在 config.yml 中设置 ipPlus360Ipv4Key 和 ipPlus360Ipv6Key");
                    MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: 当前使用 fallback 模式作为替代");
                    return false;
                }
                break;
            case "fallback":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: fallback 模式会自动切换数据源，优先使用 IPPlus");
                break;
            case "ipplus":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: IPPlus 模式完全免费，支持IPv4和IPv6，返回详细地理信息");
                break;
            case "ipinfo":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: IPinfo 模式完全免费，支持IPv4和IPv6，提供国家级别和ASN信息");
                break;
            case "ip9":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: IP9 模式完全免费，支持IPv4和IPv6");
                break;
            case "ipquery":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: IPQuery 模式完全免费，无需API密钥");
                break;
            case "whois":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: WHOIS 模式国内精度高，海外IP可能返回较少信息");
                break;
            case "ipapi":
                MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: IPAPI 模式免费但有速率限制，可能对中国IP返回403");
                break;
            case "voreapi":
                MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 警告: VoreAPI 可能已不可用，建议使用 fallback 或 ip9");
                break;
        }

        return true;
    }

    /**
     * 获取有效的数据源列表
     *
     * @return 数据源列表
     */
    public static List<String> getValidDataSources() {
        return new ArrayList<>(VALID_DATA_SOURCES);
    }

    /**
     * 获取有效的语言列表
     *
     * @return 语言列表
     */
    public static List<String> getValidLanguages() {
        return new ArrayList<>(VALID_LANGUAGES);
    }

}