# COMP 登录认证与权限

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | 基础设施 / 通用后台 |
| 复用等级 | 可参考改造 |
| 适用项目 | 多数 ERP/CRM 后台 |
| 来源路径 | `RAW/PROJECTs/project-scaffold/README.md`、`RAW/PROJECTs/zhongsheng-backend/README.md`、`RAW/PROJECTs/qmy-admin/src/api/*/auth`、`RAW/PROJECTs/qmy-admin/src/views/*/permission.ts` |

## 组件快照

- [COMPONENTS/auth-permission](../COMPONENTS/auth-permission/README.md)
- [来源映射](../COMPONENTS/auth-permission/SOURCE_MAP.md)
- [组件规范](../COMPONENTS/auth-permission/docs/spec/COMPONENT_SPEC.md)
- [API 契约](../COMPONENTS/auth-permission/docs/contracts/API_CONTRACT.md)
- [权限契约](../COMPONENTS/auth-permission/docs/contracts/PERMISSION_CONTRACT.md)
- [验收清单](../COMPONENTS/auth-permission/docs/acceptance/ACCEPTANCE.md)

## 业务目标

提供后台登录、JWT 会话、当前用户信息、菜单权限、角色权限和路由守卫能力，并保留可选扫码登录扩展入口，作为新项目的基础入口。

## 前端入口

- 登录页面：`RAW/PROJECTs/qmy-admin/src/views/admin/login/index.vue`、`src/views/sed/login/index.vue`、`src/views/zs/login/index.vue`。
- 登录 API：`/sysUser/loginByPassword`、`/sysUser/loginByScan`、`/sysUser/logout`、`/sysUser/info`。
- 租户识别：`/qiaoMoYun/tenant/getTenantId`。
- 路由守卫：`RAW/PROJECTs/qmy-admin/src/views/*/permission.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 账号密码登录 | POST | `/sysUser/loginByPassword` | `UserAuthController` / README |
| 扫码登录 | POST | `/sysUser/loginByScan` | `UserAuthController` / README |
| 退出登录 | POST | `/sysUser/logout` | `UserAuthController` |
| 当前用户 | GET | `/sysUser/info` | `SysUserController` |
| 角色分页 | POST | `/role/page` | `RoleController` |
| 菜单维护 | POST | `/menu/saveOrUpdate`、`/menu/list`、`/menu/delete` | `SystemMenuController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `user` | `user_name`、`password_hash`、`status`、`admin_flag` | 系统主账号 |
| `user_bind` | `platform`、`union_id`、`open_id`、`third_user_id` | 第三方身份绑定 |
| `auth_token` | `token_id`、`user_id`、`status` | Token 会话状态 |
| `system_menu` | 菜单编码、路径、权限标识 | 菜单权限来源 |
| `role`、`role_menu`、`user_role` | 角色、菜单、用户关系 | RBAC 基础 |

## 权限边界

- 新版后端使用 `@PreAuthorize("@ss.hasPermission(@ss.perm('...'))")` 控制操作权限。
- 匿名路径包括登录、扫码、租户查询、Swagger 和错误页。
- JWT 请求头默认 `qiaomoyun-token`，值为完整 JWT 字符串，不使用 `Bearer ` 前缀。

## 接入步骤

1. 选择 `project-scaffold` 或 `zhongsheng-backend` 作为后端认证基座。
2. 执行用户、角色、菜单、Token 相关 SQL。
3. 对齐前端 token header、登录 API、当前用户 API。
4. 配置路由守卫和菜单权限加载。
5. 按目标项目决定是否启用飞书、钉钉、企业微信扫码。

## 验收清单

- [ ] 密码登录成功并写入 token。
- [ ] 受保护接口无 token 返回 401。
- [ ] `/sysUser/info` 能返回当前用户与权限信息。
- [ ] 菜单权限能控制路由或按钮。
- [ ] 退出登录后旧 token 失效。

## 已知风险

- `qmy-admin` 前端存在多租户接口前缀，接入新后端时要统一路径。
- JWT secret 不允许代码默认值，必须通过环境变量或密钥系统注入，缺失时启动失败。
- 调试 token、扫码平台密钥不得进入代码仓库。
- 飞书/钉钉扫码是可选配置通道，企业微信扫码仍是待接入占位。
- 当前快照状态为 `reference`：登录/JWT 可优先复用，RBAC 需要按目标项目整合包名、权限码和菜单数据。
