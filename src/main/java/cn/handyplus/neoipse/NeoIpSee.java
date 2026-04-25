package cn.handyplus.neoipse;

import cn.handyplus.lib.InitApi;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.cache.CacheManager;
import cn.handyplus.neoipse.hook.PlaceholderUtil;
import cn.handyplus.neoipse.util.ConfigCheckUtil;
import cn.handyplus.neoipse.util.LanguageUtil;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 主类
 *
 * @author 滔天
 */
public class NeoIpSee extends JavaPlugin {
    public static NeoIpSee INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // 保存默认配置文件
        saveDefaultConfig();

        // 保存默认语言文件
        saveLanguageFiles();

        // 先初始化 InitApi
        InitApi initApi = InitApi.getInstance(this);

        // 然后才能使用 MessageUtil
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在初始化插件...");

        // 检查并修复配置文件
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在检查配置文件...");
        ConfigCheckUtil.checkAndFixConfig();

        // 加载缓存大小配置
        cn.handyplus.neoipse.util.IpUtil.reloadCacheSize();

        // 检查数据源配置
        if (!ConfigCheckUtil.checkDataSource()) {
            MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 提示: 数据源配置有问题，请检查控制台");
        }

        // 初始化语言系统
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在加载语言文件...");
        LanguageUtil.init();

        // 加载变量
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在注册 PlaceholderAPI 占位符...");
        PlaceholderUtil.registerPlaceholder();

        // 打印 logo
        List<String> asciiArt = Arrays.asList(
    "",
        " _   _             ___ ____   _____    ",
        "| \\ | | ___ ___ | ___| _ \\ /___/  __",
        "|  \\| |/ _ \\/ _ \\  | | |_ \\__\\/ _ \\",
        "| |\\ | __ / (_)  | | | __ /| ___ / // __/",
        "|_| \\_|\\___/\\___/ |___|_| /____/\\___/"
);
        for (String line : asciiArt) {
            MessageUtil.sendConsoleMessage(ChatColor.DARK_AQUA + line);
        }

        // 初始化
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在初始化监听器和命令...");
        initApi.initListener("cn.handyplus.neoipse.listener")
                .initCommand("cn.handyplus.neoipse.command")
                .enableSql("cn.handyplus.neoipse.enter")
                .addMetrics(16650)
                .checkVersion(true, "https://github.com/qiuxiantao/neoipse");

        // 启动缓存预热（异步进行，不阻塞服务器启动）
        CacheManager.getInstance().preheatCache();

        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 插件初始化完成！");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 命令已注册: /neoipse");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] IP定位已成功载入服务器!");
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] Author:滔天 WIKI: https://github.com/qiuxiantao/neoipse");
    }

    /**
     * 保存默认语言文件
     */
    private void saveLanguageFiles() {
        File langDir = new File(getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // 需要保存的语言文件列表
        String[] languages = {"zh_CN", "zh_TW", "en_US", "ja_JP", "ko_KR", "es_ES"};

        for (String lang : languages) {
            File langFile = new File(langDir, lang + ".yml");
            if (!langFile.exists()) {
                saveResource("languages/" + lang + ".yml", false);
            }
        }
    }

    @Override
    public void onDisable() {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 正在关闭插件...");

        // 清理缓存
        cn.handyplus.neoipse.util.IpUtil.clearAllCache();
        cn.handyplus.neoipse.constants.BaseNeoIpConstants.PLAYER_REGION_MAP.clear();
        cn.handyplus.neoipse.constants.BaseNeoIpConstants.PLAYER_SHOW_MAP.clear();

        // 打印异常统计信息
        cn.handyplus.neoipse.util.ExceptionUtil.getInstance().printExceptionStats();

        // 关闭缓存管理器的预热线程池
        CacheManager.getInstance().shutdown();

        // 关闭数据源管理的调度线程池
        cn.handyplus.neoipse.util.DataSourceManager.getInstance().shutdown();

        // 关闭HTTP工具的线程池
        cn.handyplus.neoipse.util.HttpUtil.getInstance().shutdown();

        // 关闭异步任务工具的线程池
        cn.handyplus.neoipse.util.AsyncUtil.getInstance().shutdown();

        // 关闭HTTP管理器的线程池
        cn.handyplus.neoipse.http.HttpManager.getInstance().shutdown();

        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 插件已关闭！");
    }

}
