# PERMISSION_CONTRACT｜auth-permission

## 权限模型

```text
user -> user_role -> role -> role_menu -> system_menu.permission
```

用户最终权限集合来自启用角色关联的菜单权限标识。

## 权限码规则

建议格式：

```text
模块:资源:动作
```

示例：

- `system:role:page`
- `system:menu:list`
- `system:menu:save`
- `system:menu:remove`

## 后端校验

方法级权限参考：

```java
@PreAuthorize("@ss.hasPermission(@ss.perm('MENU_LIST'))")
```

要求：

- Controller 方法必须声明权限。
- 权限码必须能从 `ApiPermissionConstants` 追溯。
- 当前用户权限集合必须来自后端，不由前端伪造。
- `*` 仅表示超级管理员全权限。

## 前端校验

前端有两层：

- 路由过滤：`permission` store 根据 `curPermissions` 过滤动态路由。
- 按钮隐藏：`v-permission` 指令隐藏无权限按钮。

前端权限只改善体验，不作为安全边界。

## 匿名路径

通常允许匿名访问：

- `/sysUser/loginByPassword`
- `/sysUser/loginByScan`
- `/qiaoMoYun/tenant/getTenantId`
- Swagger/OpenAPI 文档路径
- `/error`

生产环境需复核 Swagger 是否开放。
