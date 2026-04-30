# COMPONENT_SPEC｜auth-permission

## 目标

为 ERP/CRM 后台提供统一认证与权限骨架，使新项目可以快速获得登录、会话、用户身份、角色、菜单和按钮权限能力。

## 后端规范

### 认证层

- 登录接口放在 `/sysUser`。
- 密码登录使用 `PasswordLoginDTO`。
- 扫码登录使用 `ScanLoginDTO`，`type` 区分 `feishu`、`dingtalk`、`wecom`。
- 登录成功返回 `UserLoginVO.token`。
- 受保护接口通过 JWT 拦截器解析 token，并写入 `LoginUserInfoContext`。
- Token 会话状态落表 `auth_token`，退出登录必须吊销当前 `tokenId`。
- `auth.jwt.secret` 不允许代码默认值，必须由环境变量或密钥系统注入；未配置时启动失败。

### 扫码登录边界

- 飞书策略已带出开放平台换票与用户身份解析代码，但依赖平台 `appId`、`appSecret`、`redirectUri` 或租户配置。
- 钉钉策略已带出开放平台换票与通讯录用户解析代码，但依赖平台应用配置。
- 企业微信策略当前只保留扩展点，占位实现会返回 `SCAN_LOGIN_NOT_READY`。
- 新项目未明确启用扫码登录时，应隐藏或禁用前端扫码入口。
- 启用任何扫码通道前，都必须完成平台配置、回调域名、`user_bind` 绑定和浏览器回归。

### RBAC 层

- 菜单表 `system_menu` 保存权限标识、路由、组件、可见性和菜单类型。
- 角色表 `role` 保存角色名称、描述和启用状态。
- `role_menu` 关联角色与菜单。
- `user_role` 关联用户与角色。
- 当前用户接口应返回 `permission.curPermissions`。
- 超级权限 `*` 表示放行全部，仅限明确超级管理员。

### 方法级权限

中盛后端参考实现：

```java
@PreAuthorize("@ss.hasPermission(@ss.perm('ROLE_PAGE'))")
```

规则：

- 权限常量集中维护在 `ApiPermissionConstants`。
- `@ss.perm('字段名')` 通过字段名解析权限码。
- `@ss.hasPermission(...)` 校验当前用户权限集合。
- `ApiPermissionMenuBootstrap` 可把权限码同步成菜单占位行。

## 前端规范

- 登录页调用 `/sysUser/loginByPassword` 或 `/sysUser/loginByScan`。
- token 写入本地 auth 工具，并随请求头发送。
- 登录后调用 `/sysUser/info` 获取用户与权限。
- `permission` store 根据 `curPermissions` 过滤动态路由。
- 路由 `meta.permission` 与后端 `system_menu.permission` 对齐。
- `v-permission` 只控制按钮展示，不替代后端接口权限。

## 装配顺序

1. 接入认证 SQL 与登录后端。
2. 接入前端登录页和 token 工具。
3. 接入当前用户接口和前端用户 store。
4. 增加菜单、角色、用户角色、角色菜单表。
5. 增加权限码常量、SpEL 权限服务和方法级权限。
6. 接入角色/菜单管理页面。
7. 做登录、路由、按钮、接口越权验收。

## 已知缺口

- 该快照不是一个已合并工程，`project-scaffold` 和 `zhongsheng-rbac` 包名不同。
- `ApiPermissionConstants` 含业务域权限码，新项目应拆分或按组件裁剪。
- 前端存在多业务线目录，需要选定一套入口。
- 企业微信扫码策略仍是待接入占位，不应标记为生产可用能力。
