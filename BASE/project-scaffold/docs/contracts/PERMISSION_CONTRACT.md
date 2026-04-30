# PERMISSION_CONTRACT｜project-scaffold

## 鉴权模型

`TraceInterceptor` 先写入 `traceId`，`AuthTokenInterceptor` 再处理 JWT 鉴权。

受保护接口必须满足：

1. 请求头存在 `auth.jwt.header-name` 指定的 token。
2. token 是完整 JWT 字符串。
3. JWT 签名和过期时间有效。
4. `auth_token` 表中对应会话仍有效。
5. 成功后写入 `LoginUserInfoContext`。

## 匿名路径

源项目匿名路径包括：

- `/external/**`
- `/sysUser/loginByPassword`
- `/sysUser/loginByScan`
- `/qiaoMoYun/tenant/getTenantId`
- `/v3/api-docs/**`
- `/swagger-ui/**`
- `/swagger-ui.html`
- `/error`

## 生产加固要求

- `/external/**` 不能裸露公网；必须增加签名、IP 白名单、网关鉴权或内网隔离。
- Swagger/OpenAPI 是否公开需按项目环境区分。
- JWT secret 必须按环境独立配置。
- 禁止保留硬编码调试 token 或固定超管绕过分支。
- 前端不要自行拼装用户身份；后端以 `LoginUserInfoContext` 为准。

## 权限缺口

本基座提供登录态和基础会话，不等同于完整 RBAC 权限系统。角色、菜单、按钮权限、数据权限后续由 `auth-permission` 组件补齐。
