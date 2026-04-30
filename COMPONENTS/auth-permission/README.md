# auth-permission 认证与权限组件

## 定位

`auth-permission` 提供 ERP/CRM 后台的基础入口能力：账号密码登录、扫码登录扩展、JWT 会话、当前用户、角色、菜单、权限标识、前端路由守卫和按钮权限。

本组件是可追溯快照，不是已打包 SDK。新项目应按目标基座改造后接入。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 基础组件 |
| 依赖组件 | `BASE/project-scaffold` |
| 主要来源 | `BASE/project-scaffold`、`RAW/PROJECTs/zhongsheng-backend`、`RAW/PROJECTs/qmy-admin` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/project-scaffold/` | 登录、JWT、Token 会话、用户、拦截器、基础认证配置 |
| `backend/zhongsheng-rbac/` | 角色、菜单、用户角色、角色菜单、权限常量、方法级鉴权参考 |
| `frontend/qmy-admin/` | 多套前端登录页、auth API、路由守卫、Pinia store、角色/菜单页面 |
| `db/project-scaffold/` | 认证与用户基础表 |
| `db/zhongsheng-backend/` | 认证表、角色菜单权限表 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 验收清单 |

## 能力边界

已覆盖：

- 账号密码登录：`/sysUser/loginByPassword`
- 扫码登录扩展：`/sysUser/loginByScan`
- 退出登录：`/sysUser/logout`
- 当前用户信息：`/sysUser/info`
- JWT 签发、解析和 `auth_token` 会话状态
- `user`、`user_bind`、`user_login_log`
- `role`、`system_menu`、`role_menu`、`user_role`
- `@PreAuthorize` + `ApiPermissionConstants` 方法级权限
- 前端 `permission.ts` 路由守卫、`v-permission` 指令、权限过滤 store

待项目确认：

- 是否启用飞书、钉钉、企业微信扫码登录。
- 是否使用 `qiaomoyun-token` 作为 token header。
- 是否保留 `*` 超级权限语义。
- 角色、菜单、权限码是否沿用中盛命名，还是按新项目重建。

## 快速接入

1. 先接入 `backend/project-scaffold` 中的登录、JWT、Token 会话和当前用户。
2. 再接入 `backend/zhongsheng-rbac` 中的角色、菜单、用户角色和角色菜单关系。
3. 执行 `db/` 中的表结构，或改写为目标项目的迁移脚本。
4. 前端选择一套业务线作为参考，一般优先看 `frontend/qmy-admin/src/views/zs` 或 `src/views/admin`。
5. 对齐 `docs/contracts/API_CONTRACT.md`、`DATA_CONTRACT.md`、`PERMISSION_CONTRACT.md`。
6. 按 `docs/acceptance/ACCEPTANCE.md` 做登录、鉴权、菜单、按钮权限验收。

## 安全规则

- 不得恢复硬编码调试 token。
- 不得提交真实数据库密码、JWT secret、第三方 app secret。
- `/external/**`、Swagger、扫码回调等匿名路径必须按项目安全策略复核。
- 前端隐藏按钮不是权限边界，后端接口必须同步校验。
