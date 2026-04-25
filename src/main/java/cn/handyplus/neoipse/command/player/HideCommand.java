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
 * 隐藏命令
 *
 * @author 滔天
 */
@HandyCommand(name = "hide", permission = "neoipse.hide")
public class HideCommand {

    public boolean execute(Player player, String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] HideCommand 被触发，玩家: " + player.getName());
        
        // 获取玩家UUID
        String uuid = player.getUniqueId().toString();
        
        // 获取玩家数据
        NeoIpSeeEnter enter = NeoIpSeeService.getInstance().findByUuid(uuid);
        if (enter == null) {
            enter = new NeoIpSeeEnter();
            enter.setPlayerUuid(uuid);
            enter.setPlayerName(player.getName());
            enter.setShowEnable(false);
            NeoIpSeeService.getInstance().add(enter);
        } else if (enter.getShowEnable()) {
            enter.setShowEnable(false);
            NeoIpSeeService.getInstance().updateShowEnable(enter);
        }
        
        // 更新缓存
        BaseNeoIpConstants.PLAYER_SHOW_MAP.put(player.getUniqueId(), false);
        
        // 发送消息
        String msg = LanguageUtil.getLangMsg("hideMsg");
        MessageUtil.sendMessage(player, msg);
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] HideCommand 执行完成");
        return true;
    }

}
