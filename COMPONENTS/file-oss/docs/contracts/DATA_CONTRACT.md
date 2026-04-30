# DATA_CONTRACT｜file-oss

## system_file

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | BIGINT | 主键 ID |
| `main_type` | VARCHAR(32) | 文件主类型，对应 `SystemFileMainTypeEnum` |
| `sub_type` | VARCHAR(64) | 文件次类型，对应 `SystemFileSubTypeEnum.code` |
| `master_id` | BIGINT | 关联业务主对象 ID |
| `name` | VARCHAR(512) | 文件名称 |
| `url` | VARCHAR(2048) | 文件访问 URL |
| `file_key` | VARCHAR(1024) | OSS key，URL 域名后的路径 |
| `endpoint` | VARCHAR(512) | OSS endpoint |
| `size` | BIGINT | 文件大小，字节 |
| `type` | VARCHAR(128) | 文件 MIME 类型 |
| `create_user` / `update_user` | BIGINT | 审计用户 |
| `create_time` / `update_time` | DATETIME | 审计时间 |
| `is_deleted` | TINYINT | 逻辑删除 |
| `deleted_time` | DATETIME | 删除时间 |

索引：

- `idx_system_file_master_id`
- `idx_system_file_main_sub_is_deleted`

## tenant_config OSS 配置

| config_code | 说明 | 是否敏感 |
| :--- | :--- | :--- |
| `tenant.oss.endpoint` | OSS endpoint | 否 |
| `tenant.oss.region` | OSS region | 否 |
| `tenant.oss.access-key-id` | AssumeRole 使用的 AccessKeyId | 是 |
| `tenant.oss.access-key-secret` | AssumeRole 使用的 AccessKeySecret | 是 |
| `tenant.oss.bucket-name` | OSS bucket | 视业务而定 |
| `tenant.oss.sts-region-id` | STS region | 否 |
| `tenant.oss.sts-endpoint` | STS endpoint | 否 |
| `tenant.oss.sts-role-arn` | STS role ARN | 是 |
| `tenant.oss.sts-role-session-name` | STS session name | 否 |
| `tenant.oss.sts-duration-seconds` | STS 有效时长 | 否 |
| `tenant.oss.sts-policy` | STS 最小权限策略 | 是 |

敏感项不得写入前端、公开文档或代码常量。组件快照只保留配置项名称，不包含真实值。

## 文件类型枚举

### SystemFileMainTypeEnum

| 枚举 | 说明 |
| :--- | :--- |
| `TENANT` | 租户级资源文件 |
| `USER` | 用户维度资源 |
| `MATERIAL` | 材料维度资源 |
| `PRODUCT` | 产品维度资源 |

### SystemFileSubTypeEnum

| 枚举 | 主类型 | 说明 |
| :--- | :--- | :--- |
| `BACKGROUND` | `TENANT` | 背景图 |
| `LOGIN_LOGO` | `TENANT` | 登录 logo |
| `MENU_COLLAPSED_LOGO` | `TENANT` | 菜单折叠 logo |
| `MENU_EXPANDED_LOGO` | `TENANT` | 菜单展开 logo |
| `USER_AVATAR` | `USER` | 用户头像 |
| `MATERIAL_IMAGE` | `MATERIAL` | 材料图片 |
| `UMBRELLA_FRAME_IMAGE` | `MATERIAL` | 伞架图片 |
| `PRODUCT_IMAGE` | `PRODUCT` | 产品图片 |

## 业务引用规则

- 新业务优先保存 `system_file.id`。
- 需要多图或多附件时，优先使用 `main_type + sub_type + master_id` 的文件记录关系。
- 若历史业务表已有 URL 字段，可先保留 URL，但应同时写入 `system_file`。
- `url` 不应作为唯一长期主键，OSS 域名、CDN 域名或 bucket 迁移都会影响 URL。
