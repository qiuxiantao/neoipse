package cn.handyplus.neoipse.util;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.NeoIpSee;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 语言工具类
 *
 * @author 滔天
 */
public class LanguageUtil {

    private static final Map<String, String> MESSAGES = new HashMap<>();
    private static String currentLanguage;

    /**
     * 默认消息映射
     */
    private static final Map<String, String> DEFAULT_MESSAGES = new HashMap<>();

    static {
        DEFAULT_MESSAGES.put("noPermission", ChatColor.DARK_RED + "你没有权限执行该命令");
        DEFAULT_MESSAGES.put("reloadMsg", ChatColor.GREEN + "插件重载完成");
        DEFAULT_MESSAGES.put("noPlayerFailureMsg", ChatColor.GRAY + "[" + ChatColor.RED + "✘" + ChatColor.GRAY + "] " + ChatColor.GRAY + "该命令只能玩家执行");
        DEFAULT_MESSAGES.put("hideMsg", ChatColor.GRAY + "[" + ChatColor.GREEN + "✔" + ChatColor.GRAY + "] " + ChatColor.GREEN + "隐藏成功");
        DEFAULT_MESSAGES.put("showMsg", ChatColor.GRAY + "[" + ChatColor.GREEN + "✔" + ChatColor.GRAY + "] " + ChatColor.GREEN + "显示成功");
        DEFAULT_MESSAGES.put("toggleMsg", ChatColor.GRAY + "[" + ChatColor.GREEN + "✔" + ChatColor.GRAY + "] " + ChatColor.GREEN + "切换成功");
        DEFAULT_MESSAGES.put("queryTitle", ChatColor.GOLD + "" + ChatColor.BOLD + "=== 地域信息 === ");
        DEFAULT_MESSAGES.put("queryFullInfo", ChatColor.YELLOW + "完整信息: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryCountry", ChatColor.YELLOW + "国家: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryProvince", ChatColor.YELLOW + "省份: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryCity", ChatColor.YELLOW + "城市: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryISP", ChatColor.YELLOW + "运营商: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryDistrict", ChatColor.YELLOW + "区县: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("queryIP", ChatColor.YELLOW + "IP地址: " + ChatColor.WHITE);
        DEFAULT_MESSAGES.put("playerNotFound", ChatColor.GRAY + "[" + ChatColor.RED + "✘" + ChatColor.GRAY + "] " + ChatColor.RED + "玩家不存在");
        DEFAULT_MESSAGES.put("querySelfTitle", ChatColor.GOLD + "你的地域信息");
        DEFAULT_MESSAGES.put("queryOtherTitle", ChatColor.GOLD + "%s 的地域信息");
    }

    /**
     * 初始化语言文件
     */
    public static void init() {
        try {
            currentLanguage = ConfigCacheUtil.getLanguage();
            loadLanguageFile(currentLanguage);
            MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 语言文件加载成功: " + currentLanguage);
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 语言文件加载失败，使用默认语言: zh_CN");
            e.printStackTrace();
            loadLanguageFile("zh_CN");
        }
    }

    /**
     * 加载语言文件
     *
     * @param language 语言代码
     */
    private static void loadLanguageFile(String language) {
        MESSAGES.clear();

        InputStream input = null;
        try {
            File langFile = new File(NeoIpSee.INSTANCE.getDataFolder(), "languages" + File.separator + language + ".yml");

            FileConfiguration config;
            if (langFile.exists()) {
                config = YamlConfiguration.loadConfiguration(langFile);
            } else {
                input = NeoIpSee.INSTANCE.getResource("languages/" + language + ".yml");
                if (input != null) {
                    config = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
                } else {
                    input = NeoIpSee.INSTANCE.getResource("languages/zh_CN.yml");
                    if (input != null) {
                        config = YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8));
                    } else {
                        setDefaultMessages();
                        return;
                    }
                }
            }

            for (String key : config.getKeys(false)) {
                String value = config.getString(key);
                if (value != null) {
                    value = ChatColor.translateAlternateColorCodes('&', value);
                    MESSAGES.put(key, value);
                }
            }

            ensureAllKeysExist();

        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 加载语言文件失败: " + e.getMessage());
            setDefaultMessages();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 设置默认消息
     */
    private static void setDefaultMessages() {
        MESSAGES.clear();
        MESSAGES.putAll(DEFAULT_MESSAGES);
    }

    /**
     * 确保所有必需的键都存在
     */
    private static void ensureAllKeysExist() {
        for (Map.Entry<String, String> entry : DEFAULT_MESSAGES.entrySet()) {
            if (!MESSAGES.containsKey(entry.getKey())) {
                MESSAGES.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取语言消息
     *
     * @param key 消息键
     * @return 翻译后的消息
     */
    public static String getLangMsg(String key) {
        String message = MESSAGES.get(key);
        if (message == null) {
            return key;
        }
        return message;
    }

    /**
     * 重新加载语言文件
     */
    public static void reload() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在重新加载语言文件...");
        init();
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 语言文件重新加载完成");
    }

}