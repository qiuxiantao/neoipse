package cn.handyplus.neoipse.command.player;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.api.NeoIpSeeApi;
import cn.handyplus.neoipse.util.IpUtil;
import cn.handyplus.neoipse.util.LanguageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 查询命令
 *
 * @author 滔天
 */
@HandyCommand(name = "query", permission = "neoipse.query", aliases = {"check", "where"})
public class QueryCommand {

    public boolean execute(CommandSender sender, String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] QueryCommand 被触发，发送者: " + sender.getName());
        
        // 检查是否是玩家
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("noPlayerFailureMsg"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // 检查是否查询其他玩家
        if (args.length > 0) {
            // 检查权限
            if (!player.hasPermission("neoipse.query.other")) {
                MessageUtil.sendMessage(player, LanguageUtil.getLangMsg("noPermission"));
                return true;
            }
            
            String targetPlayerName = args[0];
            Player targetPlayer = player.getServer().getPlayer(targetPlayerName);
            
            if (targetPlayer == null) {
                MessageUtil.sendMessage(player, LanguageUtil.getLangMsg("playerNotFound"));
                return true;
            }
            
            // 异步查询目标玩家的位置
            String targetIp = IpUtil.getIp(targetPlayer);
            if (targetIp == null) {
                MessageUtil.sendMessage(player, ChatColor.RED + "无法获取玩家IP");
                return true;
            }
            
            MessageUtil.sendMessage(player, ChatColor.GRAY + "正在查询 " + targetPlayer.getName() + " 的位置信息...");
            
            // 异步查询
            IpUtil.getIpRegionAsync(targetIp, region -> {
                if (region != null) {
                    showLocationInfo(player, targetPlayer, targetIp, region);
                } else {
                    MessageUtil.sendMessage(player, ChatColor.RED + "查询失败，请稍后重试");
                }
            });
        } else {
            // 异步查询自己的位置
            String ip = IpUtil.getIp(player);
            if (ip == null) {
                MessageUtil.sendMessage(player, ChatColor.RED + "无法获取你的IP");
                return true;
            }
            
            MessageUtil.sendMessage(player, ChatColor.GRAY + "正在查询你的位置信息...");
            
            // 异步查询
            IpUtil.getIpRegionAsync(ip, region -> {
                if (region != null) {
                    showLocationInfo(player, player, ip, region);
                } else {
                    MessageUtil.sendMessage(player, ChatColor.RED + "查询失败，请稍后重试");
                }
            });
        }
        
        return true;
    }

    /**
     * 显示位置信息
     *
     * @param sender 发送者
     * @param target 目标玩家
     * @param ip IP地址
     * @param region 地域信息
     */
    private void showLocationInfo(CommandSender sender, Player target, String ip, String region) {
        String[] parts = region.split("\\|", 5);
        String country = parts.length > 0 ? parts[0] : "未知";
        String province = parts.length > 1 ? parts[1] : "未知";
        String city = parts.length > 2 ? parts[2] : "未知";
        String isp = parts.length > 3 ? parts[3] : "未知";
        String district = parts.length > 4 ? parts[4] : "未知";
        
        // 构建完整信息
        String fullInfo = country + (province != null && !"未知".equals(province) ? " " + province : "") + 
                        (city != null && !"未知".equals(city) ? " " + city : "");
        
        // 发送消息
        if (sender.getName().equals(target.getName())) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("querySelfTitle"));
        } else {
            MessageUtil.sendMessage(sender, String.format(LanguageUtil.getLangMsg("queryOtherTitle"), target.getName()));
        }
        
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryIP") + ip);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryFullInfo") + fullInfo);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryCountry") + country);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryProvince") + province);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryCity") + city);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryISP") + isp);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryDistrict") + district);
    }

}