# db

## 内容

| 文件 | 说明 |
| :--- | :--- |
| `zhongsheng-backend/init-system-file.sql` | `system_file` 文件记录表 |
| `zhongsheng-backend/init-tenant.sql` | `tenant_config` 配置表，OSS/STs 配置项依赖该表 |

## 接入注意

- `system_file.id` 是推荐业务引用键。
- `url` 用于展示或兼容历史字段，`endpoint` 与 `file_key` 用于后续域名迁移。
- `main_type`、`sub_type`、`master_id` 用于将文件挂到业务主对象。
- OSS AccessKey、RoleArn、Policy 等配置应写入安全配置源，不能写入前端或共享文档。
