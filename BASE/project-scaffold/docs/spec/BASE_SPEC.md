# BASE_SPEC｜project-scaffold

## 目标

为 ERP/CRM 新项目提供一个后端基础骨架，统一模块分层、接口返回、认证登录、租户配置、基础数据、文件 OSS、异常处理和数据库审计规则。

## 技术栈

- JDK 21
- Spring Boot 多模块 Maven
- MySQL
- MyBatis-Plus
- SpringDoc OpenAPI
- Lombok
- Hutool
- Aliyun STS SDK
- Spring Security Crypto

## 模块职责

| 模块 | 职责 |
| :--- | :--- |
| `project-api` | DTO、请求对象、统一响应、分页响应；不放 Controller |
| `project-application` | 启动类、配置绑定、拦截器、全局异常、切面、OpenAPI、日志、告警 |
| `project-common` | 错误码、枚举、常量、上下文、异常模型、工具类 |
| `project-core` | 核心业务域，按 `controller -> service -> manager -> dao` 分层 |
| `project-infrastructure` | MyBatis-Plus 基础配置、`BaseDO`、自动填充 |
| `project-test` | 测试模块 |

## 内置业务域

| 业务域 | 包路径 | 能力 |
| :--- | :--- | :--- |
| 认证 | `core.auth` | 账号密码登录、扫码登录策略、JWT、Token 会话 |
| 用户 | `core.user` | 用户信息、第三方绑定、当前用户信息 |
| 租户 | `core.tenant` | 租户配置、域名查询、租户公开信息 |
| 基础数据 | `core.base` | 树节点、业务配置值、批量保存、按节点查询 |
| 文件 | `core.file` | 系统文件记录、OSS STS 临时凭证 |
| 外部同步 | `core.external` | 中台同步租户配置与文件 |

## 分层规则

```text
controller -> service -> manager -> dao
```

- `controller`：HTTP 协议适配，只做收参、出参和 `ResultInfo` 包装。
- `service`：业务用例编排，负责事务边界。
- `manager`：领域能力层，封装单表或单聚合规则。
- `dao`：MyBatis-Plus `BaseMapper<DO>`，保持极薄。
- `external.controller` / `external.service`：面向中台或外部系统的入口，复用内部 manager，不重复建业务域。

## 新项目改名步骤

1. 修改根工程 `artifactId`。
2. 修改 `project-*` 模块前缀。
3. 修改 Java 包名 `com.qmy.project`。
4. 修改启动类、Maven module、README 与脚本中的项目名称。
5. 保留模块职责，不把业务 Controller 写入 `project-api`。
6. 根据目标业务新增领域包，例如 `core.customer`、`core.quote`、`core.order`。

## 业务组件接入方式

业务组件接入基座时遵循：

1. DTO 放入 `project-api`。
2. Controller/Service/Manager/DAO 放入 `project-core` 对应业务域。
3. 公共枚举、错误码、上下文、工具类放入 `project-common`。
4. 表结构优先复用 `BaseDO` 审计字段。
5. 跨系统同步接口统一放入 `core.external`。

## 生产化前置条件

- 创建目标环境自己的 `application-*.yml`，不得复用源项目环境配置。
- 替换 JWT secret。
- 为 `/external/**` 增加签名、白名单或网关鉴权。
- 确认租户配置由目标环境数据库的 `tenant_config` 管理。
- 删除或禁止任何调试绕过鉴权入口。
