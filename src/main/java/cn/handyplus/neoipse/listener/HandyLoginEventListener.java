package cn.handyplus.neoipse.listener;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.constants.BaseNeoIpConstants;
import cn.handyplus.neoipse.util.IpUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * 玩家登录事件
 *
 * @author 滔天
 */
public class HandyLoginEventListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }

            // 异步获取IP和地域信息
            String ip = IpUtil.getIp(player);
            if (ip == null) {
                return;
            }

            // 异步查询地域信息
            IpUtil.getIpRegionAsync(ip, region -> {
                if (region != null) {
                    // 存储地域信息
                    BaseNeoIpConstants.PLAYER_REGION_MAP.put(player.getUniqueId(), region);
                    // 默认显示
                    BaseNeoIpConstants.PLAYER_SHOW_MAP.put(player.getUniqueId(), true);
                    MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] 玩家 " + player.getName() + " 登录，IP: " + ip + "，地域: " + region);
                }
            });
        } catch (Exception e) {
            MessageUtil.sendConsoleMessage(ChatColor.RED + "[neoipSee] 登录事件处理失败: " + e.getMessage());
        }
    }

}