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
 * 显示命令
 *
 * @author 滔天
 */
@HandyCommand(name = "show", permission = "neoipse.show")
public class ShowCommand {

    public boolean execute(Player player, String[] args) {
        MessageUtil.sendConsoleMessage(ChatColor.YELLOW + "[neoipSee] ShowCommand 被触发，玩家: " + player.getName());
        
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
        } else if (!enter.getShowEnable()) {
            enter.setShowEnable(true);
            NeoIpSeeService.getInstance().updateShowEnable(enter);
        }
        
        // 更新缓存
        BaseNeoIpConstants.PLAYER_SHOW_MAP.put(player.getUniqueId(), true);
        
        // 发送消息
        String msg = LanguageUtil.getLangMsg("showMsg");
        MessageUtil.sendMessage(player, msg);
        MessageUtil.sendConsoleMessage(ChatColor.GREEN + "[neoipSee] ShowCommand 执行完成");
        return true;
    }

}
