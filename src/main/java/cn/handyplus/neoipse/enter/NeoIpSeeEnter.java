package cn.handyplus.neoipse.enter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 玩家数据实体类
 *
 * @author 滔天
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NeoIpSeeEnter {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 玩家名称
     */
    private String playerName;

    /**
     * 玩家UUID
     */
    private String playerUuid;

    /**
     * 是否显示
     */
    private Boolean showEnable;

    /**
     * 玩家IP
     */
    private String ip;

    /**
     * IP类型
     */
    private String ipType;
}
