package cn.handyplus.neoipse.hook;

import cn.handyplus.lib.util.MessageUtil;
import cn.handyplus.neoipse.api.NeoIpSeeApi;
import cn.handyplus.neoipse.constants.BaseNeoIpConstants;
import cn.handyplus.neoipse.util.RegionUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI 占位符
 *
 * @author 滔天
 */
public class PlaceholderUtil extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "neoipse";
    }

    @Override
    public @NotNull String getAuthor() {
        return "滔天";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        String region = BaseNeoIpConstants.PLAYER_REGION_MAP.get(player.getUniqueId().toString());
        if (region == null) {
            region = NeoIpSeeApi.getRegion(player);
        }

        switch (params) {
            case "":
                return region;
            case "country":
                return getRegionPart(region, 0);
            case "province":
                return getRegionPart(region, 1);
            case "city":
                return getRegionPart(region, 2);
            case "isp":
                return getRegionPart(region, 3);
            case "district":
                return getRegionPart(region, 4);
            default:
                return "";
        }
    }

    /**
     * 获取地域信息的指定部分
     *
     * @param region 地域信息
     * @param index  索引
     * @return 地域信息
     */
    private String getRegionPart(String region, int index) {
        if (region == null || "0".equals(region)) {
            return RegionUtil.getUnknownText();
        }
        String[] parts = region.split("\\|", 5);
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
     * 注册占位符
     */
    public static void registerPlaceholder() {
        if (org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            MessageUtil.sendConsoleMessage("[neoipSee] PlaceholderAPI 已找到，正在注册占位符...");
            new PlaceholderUtil().register();
            MessageUtil.sendConsoleMessage("[neoipSee] 占位符注册成功！标识符: neoipse");
        } else {
            MessageUtil.sendConsoleMessage("[neoipSee] 警告: PlaceholderAPI 未安装，占位符功能不可用！");
        }
    }

}
