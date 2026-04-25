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

## 开发者 API

### 基本用法

```java
// 获取玩家地域信息
String ip = player.getAddress().getHostAddress();
String region = NeoIpSeeApi.getRegion(ip);

// 解析地域信息
String[] regionArray = region.split("\\|");
String country = regionArray[0];
String province = regionArray[1];
String city = regionArray[2];
String isp = regionArray[3];

// 获取指定部分的地域信息
String country = NeoIpSeeApi.getNational(ip);
String province = NeoIpSeeApi.getProvincial(ip);
String city = NeoIpSeeApi.getMunicipal(ip);
String isp = NeoIpSeeApi.getServiceProvider(ip);
```

### 异步查询

```java
// 异步查询地域信息
String ip = player.getAddress().getHostAddress();
NeoIpSeeApi.getRegionAsync(ip, region -> {
    // 处理查询结果
    if (region != null) {
        // 地域信息查询成功
        player.sendMessage("您的地域信息: " + region);
    } else {
        // 地域信息查询失败
        player.sendMessage("无法查询地域信息");
    }
});
```

## 性能优化

1. **缓存策略**：多级缓存减少重复查询
2. **线程池**：统一管理异步任务，避免频繁创建线程
3. **HTTP 连接池**：使用 Java 11+ HttpClient 内置连接池
4. **速率限制**：防止 API 请求过多
5. **异步处理**：避免阻塞主线程
6. **懒加载**：按需加载数据
7. **线程安全**：使用 ConcurrentHashMap 确保线程安全
8. **Java 17 优化**：利用现代 JVM 的性能改进

## 安全性

1. **输入验证**：严格的 IP 地址验证和输入过滤
2. **API 密钥管理**：安全存储 API 密钥
3. **速率限制**：防止 API 滥用
4. **异常处理**：详细的错误信息和统计

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

## 版本兼容性

- **最低要求**: Java 17, Minecraft 1.8.8+
- **推荐版本**: Java 21, Paper 1.20+ (最新版本)

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

A: 支持 Spigot/Paper 1.8.8 及以上版本。

### Q: 需要什么版本的 Java？

A: 需要 Java 17 或更高版本。

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

### v1.0.0
- 项目正式命名为 neoipSee
- 实现基本的 IP 地域查询功能
- 支持多种数据源
- 集成 PlaceholderAPI
- 提供基本的命令系统

---

**注意**: 本插件仅供合法使用，请勿用于非法用途。使用前请确保遵守相关法律法规和服务器规定。