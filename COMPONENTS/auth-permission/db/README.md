# db

## 文件说明

| 文件 | 来源 | 内容 |
| :--- | :--- | :--- |
| `project-scaffold/init-auth.sql` | `BASE/project-scaffold` | `user`、`user_bind`、`user_login_log`、`auth_token` |
| `zhongsheng-backend/init-auth.sql` | `RAW/PROJECTs/zhongsheng-backend` | 中盛版认证表 |
| `zhongsheng-backend/init-system-menu.sql` | `RAW/PROJECTs/zhongsheng-backend` | `system_menu`、`role`、`role_menu`、`user_role` |

## 使用建议

- 新项目若只需要登录闭环，先使用 `project-scaffold/init-auth.sql`。
- 新项目若需要角色、菜单、按钮权限，再增加 `zhongsheng-backend/init-system-menu.sql`。
- 正式项目建议转为 Flyway/Liquibase 迁移，不直接手工执行散落 SQL。
- 所有唯一键继续保留 `is_deleted + deleted_time` 逻辑删除维度。
