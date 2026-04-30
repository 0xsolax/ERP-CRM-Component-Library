# SOURCE_MAP｜auth-permission

## 来源摘要

| 来源 | 用途 |
| :--- | :--- |
| `BASE/project-scaffold` | 认证基座、JWT、Token 会话、用户与登录接口 |
| `RAW/PROJECTs/zhongsheng-backend` | RBAC、菜单权限、方法级鉴权、权限常量 |
| `RAW/PROJECTs/qmy-admin` | 前端登录、auth API、路由守卫、权限 store、角色菜单页面 |

## 已复制范围

### project-scaffold

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `BASE/project-scaffold/code/project-core/.../auth` | `backend/project-scaffold/project-core/.../auth` | 登录、扫码、JWT、Token 会话 |
| `BASE/project-scaffold/code/project-core/.../user` | `backend/project-scaffold/project-core/.../user` | 当前用户、用户主档、第三方绑定 |
| `BASE/project-scaffold/code/project-application/.../interceptor` | `backend/project-scaffold/project-application/.../interceptor` | Trace 与 JWT 拦截器 |
| `BASE/project-scaffold/code/project-application/.../config` | `backend/project-scaffold/project-application/.../config` | 认证配置与密码加密 |
| `BASE/project-scaffold/docs/source/sql/init-auth.sql` | `db/project-scaffold/init-auth.sql` | 用户、绑定、登录日志、Token 表 |

### zhongsheng-backend

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-core/.../core/menu` | `backend/zhongsheng-rbac/zhongsheng-core/.../menu` | 菜单与权限标识 |
| `zhongsheng-core/.../core/user` | `backend/zhongsheng-rbac/zhongsheng-core/.../user` | 角色、用户角色、角色菜单、当前用户权限 |
| `zhongsheng-common/.../ApiPermissionConstants.java` | `backend/zhongsheng-rbac/.../constants` | 权限码统一来源 |
| `zhongsheng-application/.../SpElPermissionService.java` | `backend/zhongsheng-rbac/.../security` | `@PreAuthorize` 权限判断 Bean |
| `zhongsheng-application/.../ApiPermissionMenuBootstrap.java` | `backend/zhongsheng-rbac/.../bootstrap` | 启动时同步权限占位菜单 |
| `docs/sql/init-system-menu.sql` | `db/zhongsheng-backend/init-system-menu.sql` | `system_menu`、`role`、`role_menu`、`user_role` |

### qmy-admin

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `src/api/*/auth` | `frontend/qmy-admin/src/api/*/auth` | 登录、用户、角色、菜单、组织 API |
| `src/views/*/login` | `frontend/qmy-admin/src/views/*/login` | 多业务线登录页 |
| `src/views/*/permission.ts` | `frontend/qmy-admin/src/views/*/permission.ts` | 路由守卫 |
| `src/views/*/store` | `frontend/qmy-admin/src/views/*/store` | 用户与权限 store |
| `src/views/*/router` | `frontend/qmy-admin/src/views/*/router` | 动态路由与权限码 |
| `src/views/{admin,sed,zs}/system/{role,menu}` | `frontend/qmy-admin/src/views/{admin,sed,zs}/system/{role,menu}` | 角色、菜单维护页面 |
| `src/directives/permission` | `frontend/qmy-admin/src/directives/permission` | 按钮级权限指令 |

## 已排除或清理

| 内容 | 处理 | 原因 |
| :--- | :--- | :--- |
| `.git/`、`.DS_Store`、`target/`、`node_modules/` | 未复制 | 污染文件或依赖缓存 |
| `application-local.yml`、`application-dev.yml`、`application-prod.yml` | 未复制 | 可能包含真实环境配置 |
| `zhongsheng-application/.../AuthTokenInterceptor.java` | 未复制 | 源文件包含固定调试 token；组件使用 `BASE/project-scaffold` 已清理版本 |
| `AdminUserInitializer.java` | 未复制 | 源文件包含硬编码初始化账号密码逻辑，不进入通用组件 |
| `qmy-admin` 全量业务页面 | 未复制 | 仅保留登录、路由、权限、角色、菜单相关入口 |

## 事实与判断

- `project-scaffold` 的 `SysUserInfoVO` 默认返回 `curPermissions = ["*"]`，可用于基础登录闭环，但不是完整 RBAC。
- `zhongsheng-backend` 已具备 `role`、`system_menu`、`role_menu`、`user_role` 与 `@PreAuthorize` 权限判断。
- `ApiPermissionConstants` 含多个业务域权限码，新项目接入时应按组件拆分保留需要的权限码。
- `qmy-admin` 存在 `qmy`、`sed`、`admin`、`zs` 多套业务线，接入新项目时只应选定一套前端入口并统一 API 前缀。
