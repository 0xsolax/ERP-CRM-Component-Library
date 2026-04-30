# frontend

## 目录说明

`qmy-admin/` 保存后台前端登录与权限相关快照：

- `src/api/*/auth`：登录、用户、角色、菜单、组织 API。
- `src/views/*/login`：登录页。
- `src/views/*/permission.ts`：路由守卫。
- `src/views/*/store`：用户、权限、应用状态。
- `src/views/*/router`：静态路由、动态路由、权限码。
- `src/views/{admin,sed,zs}/system/{role,menu}`：角色和菜单维护页面。
- `src/directives/permission`：按钮权限指令。

## 使用方式

新项目只能选择一套业务线作为主入口，不能把 `qmy`、`sed`、`admin`、`zs` 全部直接混用。

推荐优先参考：

- 中盛类项目：`src/views/zs`
- 通用后台项目：`src/views/admin`

## 接入前检查

- token header 名称是否和后端一致。
- 登录 API 是否统一为 `/sysUser/loginByPassword`。
- 当前用户接口是否返回 `permission.curPermissions`。
- 路由 `meta.permission` 是否与后端 `system_menu.permission` 对齐。
- `v-permission` 是否只作为 UI 控制，后端仍需接口权限校验。
