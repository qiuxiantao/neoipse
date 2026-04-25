package cn.handyplus.neoipse.service;

import cn.handyplus.neoipse.enter.NeoIpSeeEnter;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务类
 *
 * @author 滔天
 */
public class NeoIpSeeService {
    private static class SingletonHolder {
        private static final NeoIpSeeService INSTANCE = new NeoIpSeeService();
    }

    public static NeoIpSeeService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final Map<String, NeoIpSeeEnter> playerData = new HashMap<>();

    /**
     * 通过uuid查询
     *
     * @param uuid 玩家uuid
     * @return NeoIpSeeEnter
     */
    public NeoIpSeeEnter findByUuid(String uuid) {
        return playerData.get(uuid);
    }

    /**
     * 更新玩家IP
     *
     * @param enter 玩家信息
     * @return 是否更新成功
     */
    public boolean updateIp(NeoIpSeeEnter enter) {
        playerData.put(enter.getPlayerUuid(), enter);
        return true;
    }

    /**
     * 更新玩家显示状态
     *
     * @param enter 玩家信息
     * @return 是否更新成功
     */
    public boolean updateShowEnable(NeoIpSeeEnter enter) {
        playerData.put(enter.getPlayerUuid(), enter);
        return true;
    }

    /**
     * 添加玩家信息
     *
     * @param enter 玩家信息
     * @return 是否添加成功
     */
    public boolean add(NeoIpSeeEnter enter) {
        playerData.put(enter.getPlayerUuid(), enter);
        return true;
    }

}