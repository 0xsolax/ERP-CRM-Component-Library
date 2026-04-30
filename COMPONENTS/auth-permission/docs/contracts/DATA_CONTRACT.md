# DATA_CONTRACT｜auth-permission

## 认证表

| 表 | 说明 | 关键字段 |
| :--- | :--- | :--- |
| `user` | 系统主账号 | `user_name`、`password_hash`、`status`、`admin_flag`、`avatar_file_id` |
| `user_bind` | 第三方身份绑定 | `platform`、`union_id`、`open_id`、`third_user_id` |
| `user_login_log` | 登录日志 | `login_account`、`login_type`、`login_status`、`token_id` |
| `auth_token` | Token 会话状态 | `token_id`、`user_id`、`token`、`status`、`expire_time` |

## RBAC 表

| 表 | 说明 | 关键字段 |
| :--- | :--- | :--- |
| `system_menu` | 菜单和权限标识 | `name`、`permission`、`type`、`path`、`component`、`visible` |
| `role` | 角色 | `name`、`desc`、`enabled` |
| `role_menu` | 角色与菜单关系 | `role_id`、`menu_id` |
| `user_role` | 用户与角色关系 | `user_id`、`role_id` |

## 逻辑删除与唯一键

- 表均继承或模拟 `BaseDO` 审计字段。
- 唯一键保留 `is_deleted + deleted_time` 维度。
- 删除时必须设置 `is_deleted = 1` 并写入 `deleted_time`。

## 权限返回结构

当前用户接口建议返回：

```json
{
  "user": {
    "id": "用户ID",
    "userName": "用户名",
    "roleIds": ["角色ID"],
    "roleNames": "角色名称"
  },
  "permission": {
    "curPermissions": ["system:role:page", "system:menu:list"]
  }
}
```

超级管理员可返回：

```json
{
  "permission": {
    "curPermissions": ["*"]
  }
}
```
