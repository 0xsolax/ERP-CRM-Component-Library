# backend

## 目录说明

| 目录 | 说明 |
| :--- | :--- |
| `project-scaffold/` | 基础登录、JWT、Token 会话、当前用户、认证拦截器 |
| `zhongsheng-rbac/` | 角色、菜单、权限常量、方法级鉴权、用户权限汇总 |

## 使用方式

新项目建议先接入 `project-scaffold` 认证闭环，再按 `zhongsheng-rbac` 增加 RBAC：

1. 登录接口与 JWT 会话。
2. 当前用户信息接口。
3. 角色、菜单、用户角色、角色菜单表。
4. 权限码常量与 `@PreAuthorize`。
5. 当前用户返回 `curPermissions` 给前端。

## 注意

本目录是快照证据，不保证单独编译。实际接入时需要补齐目标项目已有的 `ResultInfo`、`BaseDO`、异常工具、分页对象、MyBatis 配置等公共基础设施。
