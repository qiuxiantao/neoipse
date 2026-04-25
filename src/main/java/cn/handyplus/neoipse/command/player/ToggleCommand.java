package cn.handyplus.neoipse.command.player;

import cn.handyplus.lib.annotation.HandyCommand;
import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.constants.BaseNeoIpConstants;
import cn.handyplus.neoipse.enter.NeoIpSeeEnter;
import cn.handyplus.neoipse.service.NeoIpSeeService;
import cn.handyplus.neoipse.util.LanguageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * 切换显示命令
 *
 * @author 滔天
 */
@HandyCommand(name = "toggle", permission = "neoipse.toggle")
public class ToggleCommand {

    public boolean execute(Player player, String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] ToggleCommand 被触发，玩家: " + player.getName());
        
        // 获取玩家UUID
        String uuid = player.getUniqueId().toString();
        
        // 获取玩家数据
        NeoIpSeeEnter enter = NeoIpSeeService.getInstance().findByUuid(uuid);
        if (enter == null) {
            enter = new NeoIpSeeEnter();
            enter.setPlayerUuid(uuid);
            enter.setPlayerName(player.getName());
            enter.setShowEnable(true);
            NeoIpSeeService.getInstance().add(enter);
        }
        
        // 切换显示状态
        boolean newStatus = !enter.getShowEnable();
        enter.setShowEnable(newStatus);
        NeoIpSeeService.getInstance().updateShowEnable(enter);
        
        // 更新缓存
        BaseNeoIpConstants.PLAYER_SHOW_MAP.put(player.getUniqueId(), newStatus);
        
        // 发送消息
        String msg = LanguageUtil.getLangMsg("toggleMsg");
        MessageUtil.sendMessage(player, msg);
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] ToggleCommand 执行完成");
        return true;
    }

}
