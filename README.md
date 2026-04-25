# neoipSee

一个功能强大、高性能的 Minecraft 服务器 IP 地域查询插件，支持多种数据源和多语言配置。

## 项目简介

**neoipSee** 是一个基于 Spigot/Paper 平台的 Minecraft 插件，用于查询和显示玩家的 IP 地域信息。该插件采用模块化设计，支持多种数据源策略，包括离线查询和在线 API 查询，为服务器管理员和玩家提供准确的地域信息展示。

## 核心特性

- **多数据源支持**：集成了 IP9、IPQuery、IPInfo、IPPlus、IPAPI、WHOIS 等多个数据源
- **智能切换**：当一个数据源失败时，自动切换到其他数据源
- **高性能缓存**：内置高效缓存机制，减少 API 请求次数
- **异步处理**：使用线程池处理异步任务，避免阻塞主线程
- **多语言支持**：支持中文、英文、日文、韩文、西班牙文等多种语言
- **PlaceholderAPI 集成**：提供丰富的占位符，支持在其他插件中显示地域信息
- **完整的命令系统**：支持重载配置、切换显示状态等多种命令
- **细粒度权限控制**：完整的权限体系，支持不同级别的权限管理
- **安全输入验证**：严格的 IP 地址验证和输入过滤
- **详细的异常处理**：提供详细的错误信息和统计，便于问题排查

## 技术栈

- **Java 17**：现代 Java 开发语言，提供更好的性能和稳定性
- **Spigot/Paper API**：Minecraft 服务器插件开发框架
- **Maven**：项目构建和依赖管理
- **JSON**：数据解析
- **HTTP Client**：网络请求处理
- **PlaceholderAPI**：占位符集成

## 目录结构

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

## 安装指南

1. 下载插件 JAR 文件
2. 将 JAR 文件放入服务器的 `plugins` 目录
3. 重启服务器（需要 Java 17 或更高版本）
4. 插件会自动生成配置文件和语言文件

## 配置说明

插件启动后会生成以下配置文件：

- `config.yml`：主配置文件
- `languages/`：语言文件目录（包含多种语言配置）

### 主要配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `dataSource` | 数据源选择 | `fallback` |
| `language` | 语言选择 | `zh_CN` |
| `cache.maxSize` | 缓存最大大小 | `1000` |
| `removeProvinceAndCity` | 是否移除省市信息 | `false` |
| `unknown` | 未知值显示 | `未知` |
| `local` | 内网IP显示 | `内网IP` |

### 配置示例

```yaml
# 主配置文件 config.yml

# 数据源类型 (fallback, ipplus, ipinfo, ip9, ipquery, ipplus360, ipapi, whois, voreapi)
dataSource: fallback

# 语言选择 (zh_CN, zh_TW, en_US, ja_JP, ko_KR, es_ES)
language: zh_CN

# 缓存配置
cache:
  maxSize: 1000

# 其他配置
removeProvinceAndCity: false
unknown: "未知"
local: "内网IP"
```

## 命令说明

| 命令 | 权限 | 描述 |
|------|------|------|
| `/neoipse reload` | `neoipse.reload` | 重载插件配置 |
| `/neoipse toggle` | `neoipse.toggle` | 切换地域信息显示状态 |
| `/neoipse show` | `neoipse.show` | 显示地域信息 |
| `/neoipse hide` | `neoipse.hide` | 隐藏地域信息 |
| `/neoipse query <ip>` | `neoipse.query` | 查询指定 IP 的地域信息 |
| `/neoipse query <player>` | `neoipse.query.other` | 查询指定玩家的地域信息 |

## 权限节点

| 权限 | 描述 | 默认 |
|------|------|------|
| `neoipse.*` | 所有权限 | OP |
| `neoipse.reload` | 重载插件权限 | OP |
| `neoipse.toggle` | 切换显示状态权限 | 所有玩家 |
| `neoipse.show` | 显示地域信息权限 | 所有玩家 |
| `neoipse.hide` | 隐藏地域信息权限 | 所有玩家 |
| `neoipse.query` | 查询自己 IP 权限 | 所有玩家 |
| `neoipse.query.other` | 查询其他玩家 IP 权限 | OP |

## 占位符

| 占位符 | 描述 |
|--------|------|
| `%neoipse%` | 完整地域信息（格式：国家\|省份\|城市\|运营商\|区县） |
| `%neoipse_country%` | 显示国家名称 |
| `%neoipse_province%` | 显示省份名称 |
| `%neoipse_city%` | 显示城市名称 |
| `%neoipse_isp%` | 显示运营商名称 |
| `%neoipse_district%` | 显示区县名称 |

## 数据源配置

### 数据源列表

| 数据源 | 优先级 | 特点 |
|--------|--------|------|
| IPPlus | 100 | 完全免费，支持IPv4和IPv6，返回详细地理信息 |
| WHOIS | 85 | 国内精度高，海外IP可能返回较少信息 |
| IPInfo | 80 | 完全免费，支持IPv4和IPv6，提供国家级别和ASN信息 |
| IPQuery | 75 | 完全免费，无需API密钥 |
| IPPlus360 | 70 | 需要API密钥 |
| IPAPI | 65 | 免费但有速率限制，可能对中国IP返回403 |
| IP9 | 30 | 完全免费，支持IPv4和IPv6 |
| VoreApi | 20 | 可能已不可用 |

## 开发者 API

### API 概述

**NeoIpSeeApi** 类提供了一系列静态方法，用于查询玩家的地域信息。所有方法都支持同步和异步调用，以适应不同的使用场景。

### 同步方法

| 方法 | 描述 | 返回值 |
|------|------|--------|
| `getRegion(Player player)` | 获取玩家完整地域信息 | 地域信息字符串（格式：国家\|省份\|城市\|运营商\|区县） |
| `getNational(Player player)` | 获取玩家国家信息 | 国家名称 |
| `getProvince(Player player)` | 获取玩家省份信息 | 省份名称 |
| `getCity(Player player)` | 获取玩家城市信息 | 城市名称 |
| `getIsp(Player player)` | 获取玩家运营商信息 | 运营商名称 |
| `getDistrict(Player player)` | 获取玩家区县信息 | 区县名称 |

### 异步方法

| 方法 | 描述 | 回调参数 |
|------|------|----------|
| `getRegionAsync(Player player, Consumer<String> callback)` | 异步获取玩家完整地域信息 | 地域信息字符串 |
| `getNationalAsync(Player player, Consumer<String> callback)` | 异步获取玩家国家信息 | 国家名称 |
| `getProvinceAsync(Player player, Consumer<String> callback)` | 异步获取玩家省份信息 | 省份名称 |
| `getCityAsync(Player player, Consumer<String> callback)` | 异步获取玩家城市信息 | 城市名称 |
| `getIspAsync(Player player, Consumer<String> callback)` | 异步获取玩家运营商信息 | 运营商名称 |
| `getDistrictAsync(Player player, Consumer<String> callback)` | 异步获取玩家区县信息 | 区县名称 |

### 使用示例

#### 同步查询

```java
// 获取玩家地域信息
String region = NeoIpSeeApi.getRegion(player);
player.sendMessage("您的地域信息: " + region);

// 获取玩家国家信息
String country = NeoIpSeeApi.getNational(player);
player.sendMessage("您的国家: " + country);

// 获取玩家省份信息
String province = NeoIpSeeApi.getProvince(player);
player.sendMessage("您的省份: " + province);

// 获取玩家城市信息
String city = NeoIpSeeApi.getCity(player);
player.sendMessage("您的城市: " + city);

// 获取玩家运营商信息
String isp = NeoIpSeeApi.getIsp(player);
player.sendMessage("您的运营商: " + isp);

// 获取玩家区县信息
String district = NeoIpSeeApi.getDistrict(player);
player.sendMessage("您的区县: " + district);
```

#### 异步查询

```java
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

#### 示例 1: 在玩家加入时显示地域信息

```java
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    // 异步获取地域信息，避免阻塞主线程
    NeoIpSeeApi.getRegionAsync(player, region -> {
        player.sendMessage("欢迎来自 " + region + " 的玩家！");
    });
}
```

#### 示例 2: 创建自定义命令显示地域信息

```java
public class RegionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            // 异步获取地域信息
            NeoIpSeeApi.getRegionAsync(player, region -> {
                player.sendMessage("您的地域信息: " + region);
            });
            return true;
        }
        return false;
    }
}
```

## 性能优化

### 缓存优化

1. **智能清理机制**：使用计数器控制清理频率，避免每次操作都执行清理
2. **批量清理**：当缓存超过限制时，一次删除多个最旧的条目
3. **时间戳更新**：访问缓存时更新时间戳，延长缓存时间
4. **线程安全**：使用 ConcurrentHashMap 确保线程安全
5. **内存管理**：合理控制缓存大小，避免内存占用过高

### 网络优化

1. **HTTP 连接池**：使用 Java 11+ HttpClient 内置连接池
2. **速率限制**：实现 API 调用速率限制，防止被 API 提供商封禁
3. **异步请求**：使用线程池处理 HTTP 请求，避免阻塞主线程
4. **重试机制**：当请求失败时自动重试，提高查询成功率

### 代码优化

1. **异步处理**：将耗时操作放在后台线程中执行
2. **懒加载**：按需加载数据，避免不必要的计算
3. **异常处理**：详细的异常捕获和处理，提高代码稳定性
4. **日志记录**：详细的日志记录，便于问题排查

## 安全性

1. **输入验证**：严格的 IP 地址验证和输入过滤
2. **API 密钥管理**：安全存储 API 密钥
3. **速率限制**：防止 API 滥用
4. **异常处理**：详细的错误信息和统计
5. **数据清理**：定期清理缓存，避免内存泄漏

## 开发说明

### 构建项目

```bash
mvn clean install
```

### 运行测试

```bash
mvn test
```

### 开发环境要求

- JDK 17 或更高版本
- Maven 3.6+
- IDE（推荐 IntelliJ IDEA 或 Eclipse）

### 代码规范

1. **命名规范**：使用驼峰命名法，类名首字母大写，方法名和变量名首字母小写
2. **注释规范**：为所有类、方法添加详细的 Javadoc 注释
3. **代码风格**：保持一致的代码风格，使用 4 空格缩进
4. **异常处理**：合理处理异常，避免空捕获
5. **性能考虑**：注意代码性能，避免不必要的计算和操作

## 版本兼容性

- **最低要求**: Java 17, Minecraft 1.8.8+
- **推荐版本**: Java 21, Paper 1.21+ (最新版本)
- **支持的服务器**: Spigot, Paper, Folia

## 常见问题

### Q: 为什么地域信息显示为"未知"？

A: 可能的原因：
- IP 地址无效或无法查询
- 数据源配置错误
- API 限流或不可用
- 网络连接问题

### Q: 如何切换数据源？

A: 在 `config.yml` 中修改 `dataSource` 配置项，然后使用 `/neoipse reload` 命令重载配置。

### Q: 插件支持哪些 Minecraft 版本？

A: 支持 Spigot/Paper 1.8.8 及以上版本，推荐使用 1.21+ 版本以获得最佳性能。

### Q: 需要什么版本的 Java？

A: 需要 Java 17 或更高版本，推荐使用 Java 21 以获得最佳性能。

### Q: 如何查看插件的日志？

A: 插件的日志会输出到服务器的控制台和日志文件中，前缀为 `[neoipSee]`。

### Q: 如何报告 bug 或提出建议？

A: 请在 GitHub 仓库的 Issues 页面提交 bug 报告或功能建议。

## 贡献指南

1. **Fork 项目**：在 GitHub 上 fork 项目到自己的仓库
2. **克隆项目**：将 fork 的项目克隆到本地
3. **创建分支**：创建一个新的分支用于开发
4. **进行修改**：实现功能或修复 bug
5. **提交代码**：提交代码并编写清晰的提交信息
6. **推送分支**：将分支推送到 GitHub
7. **创建 Pull Request**：在 GitHub 上创建 Pull Request

## 技术亮点

1. **模块化设计**：将功能拆分为多个独立模块，提高代码可维护性
2. **多数据源策略**：支持多种数据源，并能智能切换
3. **高性能缓存**：优化的缓存机制，减少 API 请求次数
4. **异步处理**：使用线程池处理异步任务，避免阻塞主线程
5. **多语言支持**：支持多种语言，满足不同用户的需求
6. **PlaceholderAPI 集成**：提供丰富的占位符，支持在其他插件中显示地域信息
7. **完整的命令系统**：支持多种命令，方便用户使用
8. **细粒度权限控制**：完整的权限体系，支持不同级别的权限管理
9. **安全输入验证**：严格的 IP 地址验证和输入过滤
10. **详细的异常处理**：提供详细的错误信息和统计，便于问题排查

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 联系方式

- **作者**: 滔天
- **项目地址**: https://github.com/qiuxiantao/neoipse
- **Wiki**: https://github.com/qiuxiantao/neoipse/wiki

## 更新日志

### v2.0.0 (2026-04-25)
- 重构项目结构，采用模块化设计
- 创建 CacheManager、HttpManager、ValidationManager 等核心管理器
- 优化多数据源切换逻辑
- 提高缓存性能和可靠性
- 增强异常处理和错误信息
- 完善文档和使用示例
- 适配 Minecraft 1.21+ 版本

### v1.0.0
- 项目正式命名为 neoipSee
- 实现基本的 IP 地域查询功能
- 支持多种数据源
- 集成 PlaceholderAPI
- 提供基本的命令系统

---

**注意**: 本插件仅供合法使用，请勿用于非法用途。使用前请确保遵守相关法律法规和服务器规定。