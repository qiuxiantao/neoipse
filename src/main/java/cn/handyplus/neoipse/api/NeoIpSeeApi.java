package cn.handyplus.neoipse.api;

import cn.handyplus.neoipse.util.IpUtil;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * neoipSee API 类
 * 提供了查询玩家IP地域信息的各种方法
 *
 * <h2>使用示例：</h2>
 * <pre>
 * // 同步获取玩家地域信息
 * String region = NeoIpSeeApi.getRegion(player);
 * player.sendMessage("您的地域信息: " + region);
 *
 * // 异步获取玩家地域信息
 * NeoIpSeeApi.getRegionAsync(player, region -> {
 *     player.sendMessage("您的地域信息: " + region);
 * });
 *
 * // 获取玩家国家信息
 * String country = NeoIpSeeApi.getNational(player);
 * player.sendMessage("您的国家: " + country);
 *
 * // 异步获取玩家省份信息
 * NeoIpSeeApi.getProvinceAsync(player, province -> {
 *     player.sendMessage("您的省份: " + province);
 * });
 * </pre>
 *
 * @author 滔天
 */
public class NeoIpSeeApi {

    /**
     * 获取玩家地域信息
     *
     * @param player 玩家
     * @return 地域信息
     */
    public static String getRegion(Player player) {
        String ip = IpUtil.getIp(player);
        if (ip == null) {
            return getStr("0");
        }
        String region = IpUtil.getIpRegion(ip);
        return getStr(region);
    }

    /**
     * 异步获取玩家地域信息
     *
     * @param player 玩家
     * @param callback 回调函数，参数为地域信息
     */
    public static void getRegionAsync(Player player, Consumer<String> callback) {
        String ip = IpUtil.getIp(player);
        if (ip == null) {
            callback.accept(getStr("0"));
            return;
        }
        IpUtil.getIpRegionAsync(ip, region -> {
            callback.accept(getStr(region));
        });
    }

    /**
     * 获取玩家国家
     *
     * @param player 玩家
     * @return 国家
     */
    public static String getNational(Player player) {
        return getRegionPart(player, 0);
    }

    /**
     * 异步获取玩家国家
     *
     * @param player 玩家
     * @param callback 回调函数，参数为国家
     */
    public static void getNationalAsync(Player player, Consumer<String> callback) {
        getRegionPartAsync(player, 0, callback);
    }

    /**
     * 获取玩家省份
     *
     * @param player 玩家
     * @return 省份
     */
    public static String getProvince(Player player) {
        return getRegionPart(player, 1);
    }

    /**
     * 异步获取玩家省份
     *
     * @param player 玩家
     * @param callback 回调函数，参数为省份
     */
    public static void getProvinceAsync(Player player, Consumer<String> callback) {
        getRegionPartAsync(player, 1, callback);
    }

    /**
     * 获取玩家城市
     *
     * @param player 玩家
     * @return 城市
     */
    public static String getCity(Player player) {
        return getRegionPart(player, 2);
    }

    /**
     * 异步获取玩家城市
     *
     * @param player 玩家
     * @param callback 回调函数，参数为城市
     */
    public static void getCityAsync(Player player, Consumer<String> callback) {
        getRegionPartAsync(player, 2, callback);
    }

    /**
     * 获取玩家ISP
     *
     * @param player 玩家
     * @return ISP
     */
    public static String getIsp(Player player) {
        return getRegionPart(player, 3);
    }

    /**
     * 异步获取玩家ISP
     *
     * @param player 玩家
     * @param callback 回调函数，参数为ISP
     */
    public static void getIspAsync(Player player, Consumer<String> callback) {
        getRegionPartAsync(player, 3, callback);
    }

    /**
     * 获取玩家区县
     *
     * @param player 玩家
     * @return 区县
     */
    public static String getDistrict(Player player) {
        return getRegionPart(player, 4);
    }

    /**
     * 异步获取玩家区县
     *
     * @param player 玩家
     * @param callback 回调函数，参数为区县
     */
    public static void getDistrictAsync(Player player, Consumer<String> callback) {
        getRegionPartAsync(player, 4, callback);
    }

    /**
     * 获取地域信息的指定部分
     *
     * @param player 玩家
     * @param index 索引
     * @return 地域信息
     */
    private static String getRegionPart(Player player, int index) {
        String region = getRegion(player);
        if (region == null || "0".equals(region)) {
            return getStr("0");
        }
        String[] parts = region.split("\\|", 5);
        if (parts.length > index) {
            return getStr(parts[index]);
        }
        return getStr("0");
    }

    /**
     * 异步获取地域信息的指定部分
     *
     * @param player 玩家
     * @param index 索引
     * @param callback 回调函数，参数为地域信息
     */
    private static void getRegionPartAsync(Player player, int index, Consumer<String> callback) {
        getRegionAsync(player, region -> {
            if (region == null || "0".equals(region)) {
                callback.accept(getStr("0"));
                return;
            }
            String[] parts = region.split("\\|", 5);
            if (parts.length > index) {
                callback.accept(getStr(parts[index]));
            } else {
                callback.accept(getStr("0"));
            }
        });
    }

    /**
     * 获取字符串，如果为空则返回"0"
     *
     * @param str 字符串
     * @return 处理后的字符串
     */
    private static String getStr(String str) {
        return str == null || str.isEmpty() ? "0" : str;
    }

}