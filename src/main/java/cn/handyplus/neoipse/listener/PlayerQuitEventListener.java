package cn.handyplus.neoipse.listener;

import cn.handyplus.lib.annotation.HandyListener;
import cn.handyplus.neoipse.constants.BaseNeoIpConstants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 退出监听器
 *
 * @author 滔天
 */
@HandyListener
public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // 清理玩家缓存（不清理IP缓存，因为可能有其他玩家使用相同IP）
        BaseNeoIpConstants.PLAYER_REGION_MAP.remove(player.getUniqueId());
        BaseNeoIpConstants.PLAYER_SHOW_MAP.remove(player.getUniqueId());
    }

}
