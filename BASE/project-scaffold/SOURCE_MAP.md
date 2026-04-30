# SOURCE_MAP｜project-scaffold

## 来源摘要

| 项 | 内容 |
| :--- | :--- |
| 来源项目 | `RAW/PROJECTs/project-scaffold` |
| 快照落点 | `BASE/project-scaffold` |
| 代码快照 | `BASE/project-scaffold/code` |
| 原始文档 | `BASE/project-scaffold/docs/source` |
| 快照日期 | 2026-04-30 |

## 已复制范围

| 来源路径 | 快照路径 | 说明 |
| :--- | :--- | :--- |
| `README.md` | `code/README.md`、`docs/source/README.source.md` | 原项目说明与业务事实来源 |
| `pom.xml` | `code/pom.xml` | Maven 父工程 |
| `project-api/` | `code/project-api/` | DTO、请求对象、统一响应 |
| `project-application/` | `code/project-application/` | 启动、配置、拦截器、异常、日志、OpenAPI |
| `project-common/` | `code/project-common/` | 公共上下文、错误码、枚举、工具 |
| `project-core/` | `code/project-core/` | auth/user/tenant/base/file/external 业务域 |
| `project-infrastructure/` | `code/project-infrastructure/` | MyBatis-Plus、BaseDO、自动填充 |
| `project-test/` | `code/project-test/` | 测试模块 |
| `docs/sql/` | `code/docs/sql/`、`docs/source/sql/` | 初始化 SQL |
| `docs/sh/` | `code/docs/sh/` | 改名脚本 |
| `docs/skills/project-scaffold-coding/` | `code/docs/skills/project-scaffold-coding/`、`docs/source/skills/project-scaffold-coding/` | 原编码规范 |

## 已排除范围

| 来源路径或规则 | 原因 |
| :--- | :--- |
| `.git/` | 嵌套 Git 仓库，不进入快照库 |
| `.DS_Store` | macOS 本地文件 |
| `target/`、`node_modules/`、`build/`、`dist/` | 构建产物或依赖缓存 |
| `.idea/`、`*.iml` | IDE 本地配置 |
| `project-application/src/main/resources/application-local.yml` | 源文件包含真实环境数据库连接信息，已排除 |
| `project-application/src/main/resources/application-dev.yml` | 环境配置不进入可复用基座 |
| `project-application/src/main/resources/application-prod.yml` | 环境配置不进入可复用基座 |

## 快照内清理

| 文件 | 处理 | 原因 |
| :--- | :--- | :--- |
| `code/project-application/src/main/java/com/qmy/project/interceptor/AuthTokenInterceptor.java` | 移除硬编码调试超管 token 分支 | 防止新项目继承固定绕过鉴权入口 |
| `code/README.md`、`docs/source/README.source.md` | 脱敏固定调试 token 字面量说明 | 防止快照文档继续传播固定绕过鉴权口令 |
| `code/project-application/src/main/resources/application.yml` | 改为通过环境变量读取 profile 与 JWT 配置 | 防止默认激活已排除的本地环境配置，避免默认弱密钥 |

## 事实记录

- 项目是 JDK 21 + Spring Boot 多模块 Maven 工程。
- 默认全局上下文路径来自 `server.servlet.context-path`，原 README 描述默认为 `/api`。
- 鉴权请求头默认来自 `auth.jwt.header-name`，当前 `application.yml` 为 `qiaomoyun-token`。
- Token 会话持久化在 `auth_token` 表。
- 业务域包含 `auth`、`user`、`tenant`、`base`、`file`、`external`。
- 基础数据使用 `base_tree_node` + `base_data` 两张表表达树形元数据与具体配置值。
- 租户配置使用 `tenant_config` key-value 模型。
- OSS STS 配置从 `tenant_config` 读取。
- `/external/**` 在源项目拦截器中属于匿名路径，生产必须额外加固。

## 待确认问题

- 新项目是否统一保留 `qiaomoyun-token` 请求头，还是改为平台统一 header。
- 外部同步接口采用签名、IP 白名单、网关鉴权还是内部网络隔离。
- 初始化 SQL 后续是否继续保留裸 SQL，还是迁移成 Flyway/Liquibase。
- 扫码登录中的钉钉、企业微信策略目前是占位逻辑，后续是否作为基座必备能力继续补齐。
