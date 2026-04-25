
<div align="center">

# NeoIpSee

一个功能强大、高性能的 Minecraft 服务器 IP 地域查询插件

[![Java Version](https://img.shields.io/badge/Java-17+-green.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21+-brightgreen.svg)](https://www.spigotmc.org/)
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/qiuxiantao/neoipse)](https://github.com/qiuxiantao/neoipse/issues)
[![GitHub stars](https://img.shields.io/github/stars/qiuxiantao/neoipse)](https://github.com/qiuxiantao/neoipse/stargazers)

支持多种数据源和多语言配置，为服务器管理员和玩家提供准确的地域信息展示。

**neoipSee** 是一个基于 Spigot/Paper 平台的 Minecraft 插件，用于查询和显示玩家的 IP 地域信息。该插件采用模块化设计，支持多种数据源策略，为服务器管理员和玩家提供准确的地域信息展示。

[特性](#-特性) • [快速开始](#-快速开始) • [配置](#-配置) • [API](#-开发者-api) • [贡献](#-贡献)

</div>

---

## 🚀 特性

| 特性 | 描述 |
|------|------|
| 🌐 **多数据源支持** | IP9、IPQuery、IPInfo、IPPlus、IPAPI、WHOIS 等多个数据源 |
| 🔄 **智能切换** | 自动切换、连续失败检测、自动恢复机制 |
| ⚡ **高性能缓存** | LRU缓存、缓存预热、批量清理 |
| 🎯 **异步处理** | 线程池、HTTP连接池、不阻塞主线程 |
| 🌍 **多语言支持** | 中文、英文、日文、韩文、西班牙文 |
| 💬 **PlaceholderAPI** | 丰富的占位符，支持在其他插件中使用 |
| 🛡️ **安全验证** | 严格的IP地址验证、输入过滤、速率限制 |
| 🔧 **完整命令系统** | 重载配置、切换显示、查询信息等 |
| 👮 **权限控制** | 细粒度权限体系 |

---

## 📦 技术栈

| 技术 | 说明 |
|------|------|
| Java 17 | 现代 Java 开发语言，提供更好的性能和稳定性 |
| Spigot/Paper API | Minecraft 服务器插件开发框架 |
| Maven | 项目构建和依赖管理 |
| JSON | 数据解析 |
| HTTP Client | 网络请求处理 |
| PlaceholderAPI | 占位符集成 |

---

## 📦 快速开始

### 环境要求

- Java 17 或更高版本
- Minecraft 1.21+ (推荐 26.1.x)
- 服务器类型：Spigot / Paper / Folia

### 安装步骤

1. 从 [Releases](https://github.com/qiuxiantao/neoipse/releases) 下载插件 JAR 文件
2. 将 JAR 文件放入服务器的 `plugins` 目录
3. 重启服务器（需要 Java 17 或更高版本）
4. 插件会自动生成配置文件和语言文件

### 基础使用

```yaml
# 查看帮助
/neoipse

# 查询玩家或IP地域信息
/neoipse query <player/ip>

# 切换地域信息显示
/neoipse toggle

# 重载配置
/neoipse reload
```

---

## ⚙️ 配置

插件启动后会在 `plugins/neoipSee/` 目录下生成配置文件。

### 配置文件说明

插件启动后会生成以下配置文件：

- `config.yml`：主配置文件
- `languages/`：语言文件目录（包含多种语言配置）

### config.yml 主要配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `dataSource` | 数据源选择 (fallback/ip9/ipquery/ipplus360/ipapi/whois/voreapi) | `ip9` |
| `language` | 语言选择 (zh_CN/zh_TW/en_US/ja_JP/ko_KR/es_ES) | `zh_CN` |
| `removeProvinceAndCity` | 是否移除省份和城市信息 | `false` |
| `unknown` | 是否显示"未知"（true: 显示，false: 不显示） | `true` |
| `local` | 内网IP显示 | `"内网IP"` |
| `isCheckUpdate` | 开启检查更新 | `true` |
| `isCheckUpdateToOpMsg` | 有新版本是否发送给op提示 | `true` |
| `ipPlus360Ipv4Key` | ipplus360数据源的ipv4密钥 | `123456` |
| `ipPlus360Ipv6Key` | ipplus360数据源的ipv6密钥 | `123456` |
| `cache.maxSize` | 缓存最大大小 | `1000` |
| `cache.preheatIps` | 预热IP列表 | 公共DNS |

### 配置示例

```yaml
# 主配置文件 config.yml

# 数据源类型（推荐使用 fallback 或 ip9）
dataSource: fallback

# 语言选择
language: zh_CN

# 是否移除省份和城市信息
removeProvinceAndCity: false

# 缓存配置
cache:
  maxSize: 1000
  preheatIps:
    - "8.8.8.8"
    - "1.1.1.1"
    - "114.114.114.114"

# 是否显示"未知"（true: 显示，false: 不显示）
unknown: true

# 本地IP显示
local: "内网IP"

# 开启检查更新
isCheckUpdate: true

# 有新版本是否发送给op提示
isCheckUpdateToOpMsg: true
```

---

## 💬 命令和权限

### 命令列表

| 命令 | 权限 | 描述 |
|------|------|------|
| `/neoipse` | 无 | 主命令帮助 |
| `/neoipse reload` | `neoipse.reload` | 重载插件配置 |
| `/neoipse toggle` | `neoipse.toggle` | 切换地域信息显示状态 |
| `/neoipse show` | `neoipse.show` | 显示地域信息 |
| `/neoipse hide` | `neoipse.hide` | 隐藏地域信息 |
| `/neoipse query <ip>` | `neoipse.query` | 查询指定 IP 的地域信息 |
| `/neoipse query <player>` | `neoipse.query.other` | 查询指定玩家的地域信息 |

### 权限节点

| 权限 | 描述 | 默认 |
|------|------|------|
| `neoipse.*` | 所有权限 | OP |
| `neoipse.reload` | 重载插件权限 | OP |
| `neoipse.toggle` | 切换显示状态权限 | 所有玩家 |
| `neoipse.show` | 显示地域信息权限 | 所有玩家 |
| `neoipse.hide` | 隐藏地域信息权限 | 所有玩家 |
| `neoipse.query` | 查询自己 IP 权限 | 所有玩家 |
| `neoipse.query.other` | 查询其他玩家 IP 权限 | OP |

---

## 🪝 PlaceholderAPI 占位符

| 占位符 | 描述 |
|--------|------|
| `%neoipse%` | 完整地域信息（国家|省份|城市|运营商|区县） |
| `%neoipse_country%` | 国家名称 |
| `%neoipse_province%` | 省份名称 |
| `%neoipse_city%` | 城市名称 |
| `%neoipse_isp%` | 运营商名称 |
| `%neoipse_district%` | 区县名称 |

**注意**：当 `unknown` 配置为 `false` 时，如果某个部分为"未知"，对应的占位符将返回空字符串。

---

## 🌐 数据源

### 数据源列表

| 数据源 | 优先级 | 特点 |
|--------|--------|------|
| IPPlus | 100 | 完全免费，支持IPv4和IPv6，返回详细地理信息 |
| WHOIS | 85 | 国内精度高，海外IP可能返回较少信息 |
| IPInfo | 80 | 完全免费，支持IPv4和IPv6，提供国家级别和ASN信息 |
| IPQuery | 75 | 完全免费，无需API密钥 |
| IPPlus360 | 70 | 需要API密钥，高精度 |
| IPAPI | 65 | 免费但有速率限制，可能对中国IP返回403 |
| IP9 | 30 | 完全免费，支持IPv4和IPv6 |
| VoreApi | 20 | 可能已不可用 |

### 数据源详细说明

| 数据源 | API地址 | 费用 | 特点 |
|--------|---------|------|------|
| fallback | - | - | 自动切换模式，优先使用ip9，失败后自动尝试whois和ipquery（推荐） |
| ip9 | https://ip9.com.cn | 完全免费 | 支持IPv4和IPv6（推荐） |
| ipquery | https://ipquery.io | 完全免费 | 无需API密钥 |
| ipplus360 | https://www.ip360.net.cn | 需要配置密钥 | 高精度 |
| ipapi | https://ip-api.com | 免费有限制 | 可能有速率限制，可能对中国IP返回403 |
| whois | https://whois.pconline.com.cn | 免费 | 国内精度高，推荐作为fallback备用 |
| voreapi | https://api.vore.top | 免费 | 已验证不可用，不推荐 |

### 智能切换机制

1. **连续失败检测**：连续失败 3 次标记为不健康
2. **自动恢复**：连续成功 2 次自动恢复为健康
3. **动态权重**：根据成功率动态调整权重
4. **健康检查**：每 5 分钟自动检查数据源健康状态

---

## 🔧 开发者 API

`NeoIpSeeApi` 类提供了一系列静态方法，支持同步和异步调用。

### 同步方法

| 方法 | 描述 | 返回值 |
|------|------|--------|
| `getRegion(Player player)` | 获取玩家完整地域信息 | 地域信息字符串（格式：国家|省份|城市|运营商|区县） |
| `getNational(Player player)` | 获取玩家国家信息 | 国家名称 |
| `getProvince(Player player)` | 获取玩家省份信息 | 省份名称 |
| `getCity(Player player)` | 获取玩家城市信息 | 城市名称 |
| `getIsp(Player player)` | 获取玩家运营商信息 | 运营商名称 |
| `getDistrict(Player player)` | 获取玩家区县信息 | 区县名称 |

```java
import cn.handyplus.neoipse.api.NeoIpSeeApi;

// 获取玩家完整地域信息
String region = NeoIpSeeApi.getRegion(player);

// 获取玩家国家信息
String country = NeoIpSeeApi.getNational(player);

// 获取玩家省份信息
String province = NeoIpSeeApi.getProvince(player);

// 获取玩家城市信息
String city = NeoIpSeeApi.getCity(player);

// 获取玩家运营商信息
String isp = NeoIpSeeApi.getIsp(player);

// 获取玩家区县信息
String district = NeoIpSeeApi.getDistrict(player);
```

### 异步方法

```java
import cn.handyplus.neoipse.api.NeoIpSeeApi;

// 异步获取玩家地域信息
NeoIpSeeApi.getRegionAsync(player, region -> {
    player.sendMessage("您的地域信息: " + region);
});

// 异步获取玩家国家信息
NeoIpSeeApi.getNationalAsync(player, country -> {
    player.sendMessage("您的国家: " + country);
});

// 异步获取玩家省份信息
NeoIpSeeApi.getProvinceAsync(player, province -> {
    player.sendMessage("您的省份: " + province);
});

// 异步获取玩家城市信息
NeoIpSeeApi.getCityAsync(player, city -> {
    player.sendMessage("您的城市: " + city);
});

// 异步获取玩家运营商信息
NeoIpSeeApi.getIspAsync(player, isp -> {
    player.sendMessage("您的运营商: " + isp);
});

// 异步获取玩家区县信息
NeoIpSeeApi.getDistrictAsync(player, district -> {
    player.sendMessage("您的区县: " + district);
});
```

### 实际应用示例

#### 玩家加入时显示地域信息

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    NeoIpSeeApi.getRegionAsync(player, region -> {
        player.sendMessage("欢迎来自 " + region + " 的玩家！");
    });
}
```

#### 在 Tab 列表中显示玩家地域

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    NeoIpSeeApi.getNationalAsync(player, country -> {
        player.setPlayerListName(country + " " + player.getName());
    });
}
```

---

## 🛠️ 开发指南

### 构建项目

```bash
mvn clean install
```

### 运行测试

```bash
mvn test
```

### 项目结构

```
src/main/java/cn/handyplus/neoipse/
├── api/             # 插件API接口
├── cache/           # 缓存管理
├── command/         # 命令处理
│   ├── admin/       # 管理员命令
│   └── player/      # 玩家命令
├── constants/       # 常量定义
├── enter/           # 数据存储
├── hook/            # 插件集成
├── http/            # HTTP请求管理
├── listener/        # 事件监听器
├── service/         # 服务类
├── strategy/        # 数据源策略
│   └── impl/        # 数据源实现
├── util/            # 工具类
├── validation/      # 输入验证
└── NeoIpSee.java    # 主类
```

### 核心类

| 类 | 职责 |
|----|------|
| `NeoIpSee` | 插件主类，管理生命周期 |
| `NeoIpSeeApi` | 对外 API 接口 |
| `CacheManager` | 缓存管理，包括预热功能 |
| `HttpManager` | HTTP 请求管理，包括速率限制和重试 |
| `DataSourceManager` | 数据源管理，包括健康检查和智能切换 |
| `ValidationManager` | 输入验证，包括 IP 和 API 密钥验证 |
| `RegionUtil` | 地域信息工具类，包括未知值处理 |
| `IpUtil` | IP 查询核心工具类 |

---

## 📋 常见问题

### Q: 为什么地域信息显示为"未知"？

A: 可能的原因：
- IP 地址无效或无法查询
- 数据源配置错误
- API 限流或不可用
- 网络连接问题

### Q: 如何隐藏地域信息中的"未知"部分？

A: 在 `config.yml` 中设置 `unknown: false`，然后使用 `/neoipse reload` 命令重载配置。

### Q: 什么是缓存预热？

A: 缓存预热是在服务器启动时预先加载常用 IP 的地域信息到缓存中。这样当玩家加入时，可以更快地显示地域信息。

### Q: 如何配置多个数据源？

A: 在 `config.yml` 中设置 `dataSource` 为 `fallback`，插件会自动在多个数据源之间切换，确保查询的稳定性。

### Q: 是否支持 IPv6？

A: 是的，部分数据源支持 IPv6 地址查询，包括 IPPlus、IPInfo、IP9 等。

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 📞 联系方式

- **作者**: 滔天
- **项目地址**: https://github.com/qiuxiantao/neoipse
- **Wiki**: https://github.com/qiuxiantao/neoipse/wiki

---

## 📝 更新日志

### v2.0.4 (2026-04-26)
- 🔧 修改 unknown 配置为布尔值（true: 显示"未知"，false: 显示空字符串）
- 🐛 修复 AbstractIpDataSource 中 DataSourceManager 空指针异常
- 📚 完善文档说明

### v2.0.3 (2026-04-26)
- ✨ 实现缓存预热功能
- 🔧 智能数据源切换（连续失败检测、自动恢复、动态权重）
- 🐛 修复硬编码"未知"显示问题
- 🚀 解决 HttpUtil 和 HttpManager 代码重复问题
- 📚 完善开发者文档和示例代码

### v2.0.2 (2026-04-26)
- ✨ 添加隐藏未知地域信息功能
- 🔧 优化 PlaceholderUtil 占位符处理逻辑
- 📚 完善文档说明

### v2.0.1 (2026-04-25)
- 🔨 重构项目结构，采用模块化设计
- ✨ 创建 CacheManager、HttpManager、ValidationManager 等核心管理器
- 🔧 优化多数据源切换逻辑
- ⚡ 提高缓存性能和可靠性
- 🛡️ 增强异常处理和错误信息
- 📚 完善文档和使用示例
- 🎮 适配 Minecraft 1.21+ 版本

### v1.0.0
- 🎉 该项目正式发布
- ✨ 基本的 IP 地域查询功能
- 🌐 支持多种数据源
- 🪝 集成 PlaceholderAPI
- 💬 提供基本的命令系统

---

<div align="center">

**⚠️ 注意**: 本插件仅供合法使用，请勿用于非法用途。使用前请确保遵守相关法律法规和服务器规定。

</div>
