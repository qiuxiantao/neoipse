package cn.handyplus.neoipse.command.admin;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.util.ConfigCacheUtil;
import cn.handyplus.neoipse.util.ConfigCheckUtil;
import cn.handyplus.neoipse.util.IpUtil;
import cn.handyplus.neoipse.util.LanguageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * 重载命令
 *
 * @author 滔天
 */
@HandyCommand(name = "reload", permission = "neoipse.reload")
public class ReloadCommand {

    public boolean execute(CommandSender sender, String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] ReloadCommand 被触发，发送者: " + sender.getName());
        
        // 清理IP缓存
        IpUtil.clearAllCache();
        
        // 重载配置
        ConfigCacheUtil.reloadConfig();
        
        // 重新加载语言文件
        LanguageUtil.reload();
        
        // 发送重载成功消息
        String msg = LanguageUtil.getLangMsg("reloadMsg");
        MessageUtil.sendMessage(sender, msg);
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] ReloadCommand 执行完成");
        return true;
    }

}