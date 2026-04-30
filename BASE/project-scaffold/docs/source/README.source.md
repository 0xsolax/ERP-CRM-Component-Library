# project-scaffold

基于 `com.qmy.project` 命名空间构建的 Spring Boot 多模块脚手架，当前默认提供认证登录体系、JWT 鉴权、租户配置、飞书异常告警和 OSS 基础能力。

## 模块说明

- `project-application`：启动模块、Servlet 过滤器与 MVC 拦截器；`@ConfigurationProperties` 绑定等启动 Bean 放在此模块；
- `project-common`：跨模块共享（枚举、常量、工具、`ThreadLocal` 上下文与异常模型等)；
- `project-api`：对外部系统的 **协议契约**（DTO、`ResultInfo` 等），不含 Controller；
- `project-core`：核心业务分层，当前内置 `auth`、`user`、`tenant`、`oss` 与通用基础数据 `base`；**供外部系统调用的接口**统一放在 `com.qmy.project.core.external`（`controller` + `service`，内部再调各域 `manager`）；
- `project-infrastructure`：MyBatis-Plus 等基础设施封装
- `project-test`：测试模块

## 当前内置能力

- 账号密码登录
- 扫码登录策略扩展，飞书/钉钉为可选配置通道，企业微信为待接入占位
- JWT 签发与校验
- Token 会话持久化在 MySQL（`auth_token` 表）
- 用户表、第三方绑定表、登录日志表、Token 状态表
- `tenant_config` 租户配置表，按 `key-value` 管理租户专属配置
- 飞书异常告警
- 通用基础数据：`base_tree_node`（多业务类型共用一棵树）与 `base_data`（挂接在树节点上的多值与扩展 JSON），见下文「基础数据（`core.base`）」

## 分层约定

`project-core` 内部按业务域聚合，再在业务域下按分层组织代码：

`controller -> service -> manager -> dao`

面向外部系统（中台、合作方等）的接口：**仅**在 `com.qmy.project.core.external` 下新增 `RestController` 与对应用例 `service`，`service` 内编排并复用各域已有 `manager`。

职责约定：

- `controller`（各业务域内）：面向本应用前台的 HTTP 接口层，只做收参、出参、协议适配
- `core.external.controller` / `core.external.service`：对外部系统的 HTTP 与用例层；Controller 只做协议适配，委托 `external.service`，再由后者调用各域 `manager`
- `service`（各业务域内）：组装多个 manager 返回的业务用例，进行编排，负责事务边界和调用组合
- `manager`：领域能力层，负责业务规则、用 mybatisPlus lambdaQuery 等方式进行单表查询返回给 service 层。
- `dao`：数据访问层，接入 mybatis，负责数据库操作，尽量不写 xml sql，若想要初始化数据库数据，请使用 java 编写初始化代码，确保插入的 id 为系统生成的雪花算法

## 基础数据（`core.base`）

`project-core` 下的 `com.qmy.project.core.base` 提供**通用基础数据**能力：用**一张树表**描述「字段管理、分类」等不同业务场景下的**节点元数据**，用**一张数据表**存放挂在节点上的**具体配置值**（多列字符串 + 扩展 JSON），避免为每种字典/配置单独建表。

### 数据模型与职责

| 表 | 职责 |
|----|------|
| `base_tree_node` | 树形结构；`biz_type` 区分业务（与 `BaseTreeBizTypeEnum` 一致，如 `FIELD_MGMT`、`CATEGORY`）；`data_bind_flag` 表示该节点是否允许关联 `base_data`；根节点 `parent_id` 为 `0`。 |
| `base_data` | 业务数据行：`node_id` 指向 `base_tree_node.id`，`value1` / `value2` / `value3` 与 `ext_json`、`remark` 承载不同场景下的值与扩展。 |

DO 继承基础设施的 `BaseDO`（审计字段、逻辑删除）。初始化 DDL 见 `docs/sql/init-base-data.sql`。树节点种子由启动时 `BaseTreeNodeDataInitializer` 按 `BaseTreeNodeSeedEnum` **幂等插入**（主键由 MyBatis-Plus 雪花算法生成，与 README「分层约定」中 DAO 初始化约定一致）。

### 设计要点

1. **树与数据解耦**：树的层级、名称、排序、业务类型在 `base_tree_node` 维护；实际业务值在 `base_data`，通过 `node_id` 关联。列表查询时由 `BaseDataServiceImpl` 将树节点信息**拼装进** `BaseDataVO`（如 `bizType`、`nodeName`、`level`），前端一次拿到展示所需上下文。
2. **按业务类型约束能否挂数据**：`BaseTreeBizTypeEnum` 中 `FIELD_MGMT` 配置了「仅叶子可绑定」（`leafOnlyDataBind = true`）：若某节点下仍有未删除子节点，则不允许在该节点上绑定 `base_data`；`CATEGORY` 无此限制。保存前由 `BaseDataServiceImpl.ensureTreeNodeAllowsDataBind` 统一校验：节点须存在、`data_bind_flag = 1`，并满足上述叶子规则。
3. **协议与分层**：入参 DTO 放在 `project-api` 的 `com.qmy.project.api.dto.base`；`controller` 只做校验与 `ResultInfo` 包装；`service` 负责编排、规则校验、`batchSaveOrUpdate` 的**事务边界**（`@Transactional`，任一条失败整批回滚）；`manager` + `dao` 保持极薄，以 MyBatis-Plus `lambdaQuery` 完成单表读写。

### HTTP 接口（全局前缀默认 `/api`）

| 说明 | 方法 | 路径 |
|------|------|------|
| 单条新增或更新（无 `id` 新增，有 `id` 更新；更新时未传 `nodeId` 则保留原节点） | POST | `/baseData/saveOrUpdate` |
| 批量同上规则 | POST | `/baseData/batchSaveOrUpdate` |
| 列表（请求体可空；可按 `nodeIds` 过滤） | POST | `/baseData/list` |
| 全部树节点（下拉、展示已配置类型） | GET | `/baseData/treeNodeList` |

### 相关错误码

定义于 `project-common` → `BaseDataErrorCodeConstants`：如基础数据不存在、树节点无效、当前节点不允许绑定基础数据等，与业务校验一一对应。

## 认证体系说明

与当前代码一致的关系如下：`project-application` 负责 MVC 拦截器与 `auth.*` 配置绑定；`project-core` 提供登录 Controller、JWT 签发/校验与 Token 状态存储；`project-common` 提供 `LoginUserInfoContext` 与统一错误码。

### 代码入口（便于对照）

| 职责 | 位置 |
|------|------|
| 拦截器注册 | `project-application` → `config.InterceptorConfig` |
| Trace / JWT 拦截器 | `project-application` → `interceptor.TraceInterceptor`、`interceptor.AuthTokenInterceptor` |
| 登录、登出 | `project-core` → `core.auth.controller.UserAuthController`（类上 `@RequestMapping("/sysUser")`） |
| 当前用户 | `project-core` → `core.user.controller.SysUserController`（`/sysUser/info`） |
| JWT 与登录用户解析 | `project-core` → `core.auth.manager.impl.AuthManagerImpl`、`core.auth.support.JwtTokenService`、`core.auth.manager.impl.AuthTokenManagerImpl` |
| 登录态 ThreadLocal | `project-common` → `common.context.LoginUserInfoContext`（业务中需登录处可 `requireLoginUserInfo()`） |

### HTTP 接口一览

全局前缀由 `server.servlet.context-path` 决定，默认 **`/api`**。下表路径均省略此前缀（例如完整地址为 `POST /api/sysUser/loginByPassword`）。

| 说明 | 方法 | 路径 | 是否需 JWT |
|------|------|------|-------------|
| 账号密码登录 | POST | `/sysUser/loginByPassword` | 否 |
| 扫码登录 | POST | `/sysUser/loginByScan` | 否 |
| 退出登录 | POST | `/sysUser/logout` | 是 |
| 当前登录用户信息 | GET | `/sysUser/info` | 是 |
| 按域名查询租户展示信息 | GET | `/qiaoMoYun/tenant/getTenantId`（Query：`domainName`） | 否 |
| 中台同步租户配置与文件 | POST | `/external/tenant/sync` | 否（`/external/**` 已排除鉴权，生产建议自行加固） |
| OSS STS 临时凭证 | GET | `/oss/getOssToken` | 是 |

登录请求体（`project-api` DTO）：`PasswordLoginDTO` 为 **`userName`**、`password`；`ScanLoginDTO` 为 **`type`**（`feishu` / `dingtalk` / `wecom`）、**`code`**（第三方授权码）。成功时返回 `UserLoginVO`，其中 **`token`** 字段即后续受保护接口应在请求头中携带的访问令牌。

Swagger / OpenAPI：`/swagger-ui.html`、`/v3/api-docs/**` 等已在拦截器中排除，无需 JWT。

### JWT 鉴权行为

- **`TraceInterceptor`**（order 0）：写入 MDC `traceId` 与响应头 `X-Trace-Id`。
- **`AuthTokenInterceptor`**（order 1）：对未排除的路径要求有效令牌；解析 JWT（签名、过期），并校验 `auth_token` 表中的会话状态后，将 **`LoginUserInfo`** 写入 **`LoginUserInfoContext`**。
- **请求头名**：`auth.jwt.header-name`，默认 **`qiaomoyun-token`**；值为**完整 JWT 字符串**（仅 `trim`，**不支持** `Bearer ` 等前缀，客户端请勿加前缀）。
- **常见错误码**：缺 token 多为 **`401`**（`GlobalErrorCodeConstants.UNAUTHORIZED`）；JWT 或会话无效多为 **`40103`**（`AuthErrorCodeConstants.TOKEN_INVALID`，文案「登录状态无效」）。
- **匿名路径**（与 `InterceptorConfig.ANONYMOUS_PATH_PATTERNS` 保持一致）：`/external/**`、`/sysUser/loginByPassword`、`/sysUser/loginByScan`、`/qiaoMoYun/tenant/getTenantId`、`/v3/api-docs/**`、`/swagger-ui/**`、`/swagger-ui.html`、`/error`。
- **调试**：源项目曾包含固定调试超管 token 分支；本快照已移除该执行分支，后续新项目不得恢复硬编码绕过鉴权入口。
- **MyBatis 填充**：`project-infrastructure` → `MybatisMetaObjectHandler` 使用 `LoginUserInfoContext.currentUserIdOrDefault(-1L)` 回填 `createUser` / `updateUser`；无登录态或 `userId` 为空时为 **`-1L`**（与库表审计字段约定一致）。

### Token 状态存储

登录会话与 JWT 载荷中的 `tokenId` 一一对应，持久化在表 **`auth_token`**（见 `AuthTokenStateManager` / `AuthTokenManagerImpl`）。

### 扫码登录策略

实现位于 `project-core` → `core.auth.strategy`，按策略模式拆分：

- `FeishuScanLoginStrategy`
- `DingtalkScanLoginStrategy`
- `WecomScanLoginStrategy`

**飞书**：已对接开放平台换票与用户身份；`redirect_uri` 优先取请求头 **`Origin` + `/login`**，否则使用 `auth.scan.feishu.redirect-uri`。应用 ID/Secret 可从 **`tenant_config`**（`tenant.account-system.feishu-app-id` / `tenant.account-system.feishu-app-secret`）与 `application.yml` 中 `auth.scan.feishu` 组合读取（见 `FeishuScanLoginStrategy`）。

**钉钉**：已保留开放平台换票与用户解析路径，启用前必须配置应用 ID/Secret、回调域名与用户绑定。

**企业微信**：当前为占位实现，调用时提示尚未完成平台配置，需自行补全换票与用户绑定逻辑。

## 数据表设计

初始化 SQL 位于：

- `docs/sql/init-auth.sql`（用户、绑定、登录日志、Token 状态等）
- `docs/sql/init-tenant.sql`（租户配置等）

### 用户（user）与第三方绑定（user_bind）

#### 职责划分

- **`user`**：系统内的**主账号**，存放登录名、密码哈希（`password_hash`）、本系统侧昵称/邮箱/手机、账号状态、性别、`admin_flag` 等。**账号密码登录**只访问 `user`，不经过 `user_bind`。
- **`user_bind`**：描述「**某一第三方平台下的身份**」与 `user.id` 的关联。典型字段包括 `platform`、`union_id` / `open_id` / `third_user_id`、第三方昵称、最近授权时间等。**扫码登录**时根据开放平台换票得到的身份，在 `user_bind` 中解析出 `user_id`，再加载 `user`。**展示头像落在 `user.avatar_file_id`**，扫码同步时由 `UserManager.syncProfile` 写入。

相关实现主要在 `project-core` → `core.user.manager`（`UserManager` / `UserBindManager` 及其实现类）。

#### 一对多、一对一分别指什么

- **第三方身份 → 用户（多对一）**：在同一个 `platform` 下，`union_id`（或 `open_id` / `third_user_id`，视平台而定）通过表上的唯一约束，**全局至多对应一行** `user_bind`，因而至多对应 **一个** `user_id`。这样保证「同一个飞书号」不会同时绑在两个不同主账号上，登录结果确定。
- **用户 → 第三方绑定（可 1:1 也可 1:N）**  
  - **一对多（1:N）**：一个 `user` 允许挂**多条** `user_bind`（例如同一员工既绑飞书又绑钉钉，用任意一端扫码都能进同一主账号）。  
  - **一对一（1:1）**：一个 `user` **最多允许一条** `user_bind`，等价于「每人只能绑一个第三方入口」。

#### 登录主体与「一对多 / 一对一」由谁决定

- **会话与 JWT 始终以 `user` 为准**（`user_id` 在 Token 载荷与 `LoginUserInfoContext` 中标识当前主账号）；`user_bind` 只负责把第三方身份解析到某个 `user_id`。
- **是否允许「一个 user 多条绑定」仅由 `user_bind` 表唯一键决定**，应用层**不再**在 `save` 前按 `userId` 预查拦截。插入违反唯一约束时返回 **`40911`**（`USER_BIND_UNIQUE_VIOLATION`）。
- **`init-auth.sql` 默认**使用 **`uk_user_bind_user_platform_is_deleted`**（`user_id`, `platform`, `is_deleted`, `deleted_time`）：同一用户可在**不同** `platform` 各有一条绑定（**一对多，按平台 1:1**）。若需**全局每用户仅一条**绑定，按脚本内注释改为 **`uk_user_bind_user_is_deleted`**（`user_id`, `is_deleted`, `deleted_time`）即可，**无需改 Java**。
- **`listByUserId`**：返回该用户下全部绑定；**`listBoundPlatforms`**：汇总已绑平台；
- **头像**：仅存 **`user.avatar_file_id`**（关联 `system_file`）；**`UserManager.resolveAvatarUrl(UserDO)`** 解析展示 URL；JWT 解析登录态时同样走该字段。

#### `user` 表核心字段（与 `init-auth.sql` 一致）

- `user_name`：登录用户名（逻辑删除维度下唯一）
- `password_hash`：密码哈希（BCrypt 等）；仅密码登录需要
- `nick_name`、`email`、`mobile`：本系统展示与联系信息（`email` / `mobile` 在删除维度下唯一，可为空）
- `status`：`0` 正常，`1` 停用
- `gender`：`0` 未知，`1` 男，`2` 女
- `admin_flag`：`0` 普通用户，`1` 超级管理员（可与中台同步逻辑配合）
- `avatar_file_id`：头像文件 ID，对应 `system_file.id`，展示 URL 见 `system_file.url`（由 `UserManager.resolveAvatarUrl` 解析）

#### `user_bind` 表要点

- 通过 `platform` + `union_id` / `open_id` / `third_user_id` 与逻辑删除字段组合唯一，保证第三方身份与绑定行一一对应；
- 扫码登录若找不到绑定，当前会报 **`ACCOUNT_NOT_EXISTS`**（不自动注册）；需要先通过运营开户、自助绑定流程等写入 `user` 与 `user_bind`。

### 唯一键规范

- 统一按 “业务字段 + is_deleted + deleted_time” 设计
- 第三方绑定表中的 `platform + union_id`、`platform + open_id`、`platform + third_user_id` 也遵循同样规则
- 执行逻辑删除时，必须同时正确写入 `is_deleted=1` 和 `deleted_time=删除时间`

这样做的目的是在逻辑删除后允许重新创建同业务值的数据，同时保留历史删除记录。

## 配置说明

`application.yml` 里只保留通用运行参数，业务密切相关的租户配置落库到 `tenant_config`：
比如：
- `tenant.name`
- `tenant.domain.name`
- `tenant.frontend-route`
- `tenant.domain-name`
- `tenant.feishu-webhook-url`
- `tenant.oss.endpoint`
- `tenant.oss.region`
- `tenant.oss.access-key-id`
- `tenant.oss.access-key-secret`
- `tenant.oss.bucket-name`
- `tenant.oss.sts-region-id`
- `tenant.oss.sts-endpoint`
- `tenant.oss.sts-role-arn`
- `tenant.oss.sts-role-session-name`
- `tenant.oss.sts-duration-seconds`
- `tenant.oss.sts-policy`

保留在 `application.yml` 的配置主要是认证和基础运行参数：

```yml
auth:
  jwt:
    issuer: project-scaffold
    secret: ${AUTH_JWT_SECRET}
    access-token-expire-seconds: 7200
    header-name: qiaomoyun-token
  scan:
    feishu:
      app-id:
      app-secret:
      redirect-uri:
    dingtalk:
      app-id:
      app-secret:
    wecom:
      corp-id:
      agent-id:
      app-secret:
```

说明：

- `auth.jwt.secret` 必须通过环境变量或密钥系统注入；未配置时应用启动失败。`header-name` 与前端或网关约定保持一致即可（token 无前缀，头内即完整 JWT）。
- Token 会话依赖 `auth_token` 表，需先执行初始化 SQL
- OSS STS、飞书告警、域名等租户专属参数不再通过 `yml` 管理，而是读取 `tenant_config`
- 生产库和测试库直接通过各自数据库里的 `tenant_config` 数据做区分
- 飞书告警只对未捕获系统异常生效

## 飞书异常告警

未捕获系统异常会按 `tenant_config` 中的 `tenant.feishu-webhook-url` 配置发送飞书告警。消息包含：

- 应用名
- 环境
- `traceId`
- 请求方法、请求地址、客户端 IP
- 异常类型、异常信息、根因摘要
- 报错位置与前 5 条堆栈摘要

接口返回仍保持统一 `ApiResponse<T>` 格式，客户端会收到 `500` 和对应 `traceId`，可直接与飞书告警和服务日志关联排查。

## 租户配置

当前租户前端路由固定为 `/qiaoMoYun/tenant`，后端接口随全局前缀暴露为 `/api/qiaoMoYun/tenant/*`。

`tenant_config` 采用 `key-value` 设计，遵循以下规范：

- 表字段使用 `config_code`、`config_name`、`config_value`、`config_remark`
- 唯一键使用 `config_code + is_deleted + deleted_time`
- 逻辑删除时必须同时写入 `is_deleted=1` 和 `deleted_time`
- 一个数据库环境维护自己的一组租户配置，不再额外依赖 `dev/prod` 的 `yml` 差异

当前代码会把 `tenant_config` 的 key-value 聚合成结构化对象，对外仍通过 `TenantConfigVO` 提供配置详情，对内则给飞书告警和 OSS STS 直接消费。

## 启动说明

运行环境要求：`JDK 21`

先初始化数据库表，再启动应用。根目录常用命令：

```bash
mvn clean test
mvn -pl project-application spring-boot:run
```

切换环境可使用 Spring Profile：

```bash
mvn -pl project-application spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl project-application spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl project-application spring-boot:run -Dspring-boot.run.profiles=prod
```

## 从模板复制新项目（一键脚本）

在**未改名的模板仓库**（根 `artifactId` 为 `project-scaffold`、模块为 `project-*`）内使用脚本即可；脚本**不会修改**当前模板目录，只会在**新路径**生成一份已改名的副本。

一键脚本按运行环境分目录存放，避免混用：

- **`docs/sh/unix/`**：macOS / Linux（Bash + `rsync`）
- **`docs/sh/windows/`**：Windows（PowerShell + `robocopy`）

### 如何执行

| 环境 | 命令或操作 |
|------|------------|
| **macOS / Linux** | 在模板仓库**根目录**执行：`bash docs/sh/unix/rename-project.sh`（需已安装 `git`、`rsync`） |
| **Windows** | 任选其一：<br>• 双击 `docs/sh/windows/rename-project.cmd`（失败时会 `pause`，避免窗口一闪而过）；<br>• 或在 PowerShell /「终端」中：`powershell -NoProfile -ExecutionPolicy Bypass -File .\docs\sh\windows\rename-project.ps1`<br><br>Windows 脚本使用系统自带的 **PowerShell** 与 **robocopy** 复制，**不需要** Git Bash、rsync 或 WSL。若提示无法运行脚本，多半是执行策略限制，请使用上面带 `-ExecutionPolicy Bypass` 的命令。 |

**编码说明（若出现乱码、`字符串缺少终止符`、`表达式或语句中包含意外的标记`）**：`rename-project.ps1` 须以 **UTF-8（带 BOM）** 保存；Windows PowerShell 对**无 BOM** 的 `.ps1` 会按系统代码页（如 GBK）解析，易导致整文件解析失败。请在编辑器中「另存为」**UTF-8 with BOM**（VS Code 右下角编码 → Save with Encoding）。

Windows 版与 Bash 版**交互项与改名规则一致**（新项目根目录名、模块前缀、目标路径、确认、远程地址等）。

### 脚本会做什么

1. **复制**整份代码到指定路径（排除 `.git`、`target`，Windows 版同时排除 `node_modules`）；
2. 在新目录内完成改名（根 `artifactId`、`project-*` 模块、`com.qmy.project` 包、`docs/skills/project-scaffold-coding`、README 中与模板复制相关的段落等）；
3. **不修改** `application*.yml` 中的数据源、JDBC、库名（请在新项目里自行配置数据库）；
4. **Git（可选）**：若系统 **`PATH` 中能执行 `git`**，则在新目录执行 **`git init -b master`**，按 **`.gitignore`** 执行 **`git add .`**，首次提交信息为 **`chore: 初始化<新项目根目录名> 项目`**；若**未安装或未加入 PATH**（常见于仅完成改名、稍后再装 Git 的场景），脚本会**跳过**初始化与提交，并打印可手动执行的 `git` 命令；
5. 提示输入**远程仓库地址**；在已执行 Git 步骤的前提下，若填写则 `git remote add origin` 并尝试 **`git push -u origin master`**，留空则仅本地提交（或无 Git 时略过），可稍后自行 `remote` + `push`。

默认新路径为与模板目录**同级**的 `<新项目根目录名>`，也可在交互中自定义绝对或相对路径。

完成后建议在新项目目录执行：`mvn -q -DskipTests compile`。

### 手动改名时要对齐的内容（脚本已覆盖大部分）

若不用脚本，需自行保持模块名、包路径、`pom.xml`、启动类与配置一致，例如将 `project-*` 与 `com.qmy.project` 全部替换为你的业务前缀与包名。
