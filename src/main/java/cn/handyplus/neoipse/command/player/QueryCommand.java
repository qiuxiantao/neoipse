package cn.handyplus.neoipse.command.player;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.util.IpUtil;
import cn.handyplus.neoipse.util.LanguageUtil;
import cn.handyplus.neoipse.util.RegionUtil;
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
                    // 检查发送者是否仍然在线
                    if (player.isOnline()) {
                        showLocationInfo(player, targetPlayer, targetIp, region);
                    }
                } else {
                    // 检查发送者是否仍然在线
                    if (player.isOnline()) {
                        MessageUtil.sendMessage(player, ChatColor.RED + "查询失败，请稍后重试");
                    }
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
                    // 检查玩家是否仍然在线
                    if (player.isOnline()) {
                        showLocationInfo(player, player, ip, region);
                    }
                } else {
                    // 检查玩家是否仍然在线
                    if (player.isOnline()) {
                        MessageUtil.sendMessage(player, ChatColor.RED + "查询失败，请稍后重试");
                    }
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
        String country = getRegionPart(parts, 0);
        String province = getRegionPart(parts, 1);
        String city = getRegionPart(parts, 2);
        String isp = getRegionPart(parts, 3);
        String district = getRegionPart(parts, 4);

        // 构建完整信息
        String fullInfo = buildFullInfo(country, province, city);

        // 发送消息
        if (sender.getName().equals(target.getName())) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("querySelfTitle"));
        } else {
            MessageUtil.sendMessage(sender, String.format(LanguageUtil.getLangMsg("queryOtherTitle"), target.getName()));
        }

        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryIP") + ip);
        MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryFullInfo") + fullInfo);

        // 只发送非空的信息
        if (!country.isEmpty()) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryCountry") + country);
        }
        if (!province.isEmpty()) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryProvince") + province);
        }
        if (!city.isEmpty()) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryCity") + city);
        }
        if (!isp.isEmpty()) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryISP") + isp);
        }
        if (!district.isEmpty()) {
            MessageUtil.sendMessage(sender, LanguageUtil.getLangMsg("queryDistrict") + district);
        }
    }

    /**
     * 获取地域信息的指定部分
     *
     * @param parts 地域信息数组
     * @param index 索引
     * @return 地域信息部分
     */
    private String getRegionPart(String[] parts, int index) {
        if (parts.length > index) {
            String value = parts[index];
            if (RegionUtil.isUnknownValue(value)) {
                return RegionUtil.getUnknownText();
            }
            return value;
        }
        return RegionUtil.getUnknownText();
    }

    /**
     * 构建完整地域信息
     *
     * @param country 国家
     * @param province 省份
     * @param city 城市
     * @return 完整地域信息
     */
    private String buildFullInfo(String country, String province, String city) {
        StringBuilder sb = new StringBuilder();
        if (!country.isEmpty()) {
            sb.append(country);
        }
        if (!province.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(province);
        }
        if (!city.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(city);
        }
        if (sb.length() == 0) {
            return RegionUtil.getUnknownText();
        }
        return sb.toString();
    }

}
