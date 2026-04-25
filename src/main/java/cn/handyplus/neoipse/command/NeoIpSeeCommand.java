package cn.handyplus.neoipse.command;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.command.admin.ReloadCommand;
import cn.handyplus.neoipse.command.player.HideCommand;
import cn.handyplus.neoipse.command.player.ShowCommand;
import cn.handyplus.neoipse.command.player.ToggleCommand;
import cn.handyplus.neoipse.command.player.QueryCommand;
import cn.handyplus.neoipse.constants.TabListEnum;
import cn.handyplus.neoipse.util.LanguageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 命令
 *
 * @author 滔天
 */
@HandyCommand(name = "neoipse")
public class NeoIpSeeCommand implements TabExecutor {

    private final HideCommand hideCommand = new HideCommand();
    private final ShowCommand showCommand = new ShowCommand();
    private final ToggleCommand toggleCommand = new ToggleCommand();
    private final ReloadCommand reloadCommand = new ReloadCommand();
    private final QueryCommand queryCommand = new QueryCommand();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 收到命令: /" + label + " " + String.join(" ", args));
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 命令发送者: " + sender.getName());
        
        // 判断指令是否正确
        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] 准备执行子命令: " + subCommand);
        
        // 直接处理子命令
        switch (subCommand) {
            case "reload":
                if (sender.hasPermission("neoipse.reload")) {
                    reloadCommand.execute(sender, args);
                } else {
                    MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPermission"));
                }
                break;
            case "toggle":
                if (sender instanceof Player) {
                    if (sender.hasPermission("neoipse.toggle")) {
                        toggleCommand.execute((Player) sender, args);
                    } else {
                        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPermission"));
                    }
                } else {
                    MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPlayerFailureMsg"));
                }
                break;
            case "show":
                if (sender instanceof Player) {
                    if (sender.hasPermission("neoipse.show")) {
                        showCommand.execute((Player) sender, args);
                    } else {
                        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPermission"));
                    }
                } else {
                    MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPlayerFailureMsg"));
                }
                break;
            case "hide":
                if (sender instanceof Player) {
                    if (sender.hasPermission("neoipse.hide")) {
                        hideCommand.execute((Player) sender, args);
                    } else {
                        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPermission"));
                    }
                } else {
                    MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPlayerFailureMsg"));
                }
                break;
            case "query":
            case "check":
            case "where":
                // 处理查询命令，支持query/check/where三个别名
                if (sender.hasPermission("neoipse.query")) {
                    queryCommand.execute(sender, args.length > 1 ? new String[]{args[1]} : new String[0]);
                } else {
                    MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPermission"));
                }
                break;
            default:
                sendHelpMessage(sender);
                break;
        }
        
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 命令处理完成");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = TabListEnum.returnList(args, args.length);
        if (commands == null || commands.isEmpty()) {
            return null;
        }
        // 过滤掉玩家没有权限的命令
        List<String> allowedCommands = new ArrayList<>();
        for (String command : commands) {
            if (sender.hasPermission("neoipse." + command)) {
                allowedCommands.add(command);
            }
        }
        StringUtil.copyPartialMatches(args[args.length - 1].toLowerCase(), allowedCommands, completions);
        Collections.sort(completions);
        return completions;
    }

    /**
     * 发送帮助信息
     *
     * @param sender 命令发送者
     */
    private void sendHelpMessage(CommandSender sender) {
        MessageUtil.sendMessage(sender, "&6&lneoipSee &7- &f玩家地域查询插件");
        MessageUtil.sendMessage(sender, "");
        if (sender.hasPermission("neoipse.reload")) {
            MessageUtil.sendMessage(sender, "&7/neoipse reload &8- &f重载插件配置");
        }
        if (sender.hasPermission("neoipse.toggle")) {
            MessageUtil.sendMessage(sender, "&7/neoipse toggle &8- &f切换地域信息显示");
        }
        if (sender.hasPermission("neoipse.show")) {
            MessageUtil.sendMessage(sender, "&7/neoipse show &8- &f显示地域信息");
        }
        if (sender.hasPermission("neoipse.hide")) {
            MessageUtil.sendMessage(sender, "&7/neoipse hide &8- &f隐藏地域信息");
        }
        if (sender.hasPermission("neoipse.query")) {
            MessageUtil.sendMessage(sender, "&7/neoipse query [玩家名] &8- &f查询地域信息");
            MessageUtil.sendMessage(sender, "&7/neoipse check [玩家名] &8- &f查询地域信息");
            MessageUtil.sendMessage(sender, "&7/neoipse where [玩家名] &8- &f查询地域信息");
        }
    }

}
