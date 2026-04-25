package cn.handyplus.neoipse.constants;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础常量类
 *
 * @author 滔天
 */
public abstract class BaseNeoIpConstants {
    /**
     * 玩家地域
     */
    public final static Map<UUID, String> PLAYER_REGION_MAP = new ConcurrentHashMap<>();

    /**
     * 是否显示
     */
    public final static Map<UUID, Boolean> PLAYER_SHOW_MAP = new ConcurrentHashMap<>();

    /**
     * 未知
     */
    public final static String UNKNOWN = "未知";

}
