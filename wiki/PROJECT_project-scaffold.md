# PROJECT project-scaffold

## 定位

`project-scaffold` 是 Spring Boot 多模块后端脚手架来源项目。它对 ERP/CRM 组件库的主要价值是后端基座、统一协议契约、认证鉴权、租户配置、OSS、基础数据和分层规范。

## 来源路径

- `RAW/PROJECTs/project-scaffold/README.md`
- `RAW/PROJECTs/project-scaffold/project-api`
- `RAW/PROJECTs/project-scaffold/project-core`
- `RAW/PROJECTs/project-scaffold/project-common`
- `RAW/PROJECTs/project-scaffold/project-infrastructure`
- `RAW/PROJECTs/project-scaffold/docs/sql`

## 已确认能力

- 账号密码登录。
- 扫码登录策略扩展，默认支持 `feishu`、`dingtalk`、`wecom`。
- JWT 签发、解析与 Token 会话持久化。
- 用户、第三方绑定、登录日志、Token 状态表。
- `tenant_config` 租户配置。
- 飞书异常告警。
- OSS STS 临时凭证。
- 通用基础数据 `base_tree_node` + `base_data`。

## 后端分层契约

README 明确后端按 `controller -> service -> manager -> dao` 组织；对外部系统接口统一放在 `core.external.controller` 与 `core.external.service`，协议 DTO 放在 `project-api`。

## 可贡献组件

| 组件 | 证据 | 复用价值 |
| :--- | :--- | :--- |
| 登录认证与权限 | README 认证体系、`docs/sql/init-auth.sql` | 新项目认证基座 |
| 产品物料基础数据 | README 基础数据、`docs/sql/init-base-data.sql` | 通用字典/分类/字段管理基座 |
| 文件上传与 OSS | README OSS、`docs/sql/init-system-file.sql` | 文件存储能力 |
| 租户配置 | README `tenant_config`、`docs/sql/init-tenant.sql` | 多客户项目环境隔离 |

## 待验证

- 该项目是通用脚手架，不包含完整客户、报价、订单、采购、仓储业务闭环。
- 新项目若采用该基座，需要再装配具体业务模块。

