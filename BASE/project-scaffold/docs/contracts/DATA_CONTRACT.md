# DATA_CONTRACT｜project-scaffold

## 通用审计字段

业务表基于 `BaseDO` 约定：

| 字段 | 说明 |
| :--- | :--- |
| `id` | 雪花算法主键 |
| `create_user` | 创建人，默认无登录态为 `-1` |
| `update_user` | 更新人，默认无登录态为 `-1` |
| `create_time` | 创建时间 |
| `update_time` | 更新时间 |
| `is_deleted` | 逻辑删除标记 |
| `deleted_time` | 逻辑删除时间 |

唯一键统一建议包含 `is_deleted` 与 `deleted_time`，保证逻辑删除后可重新创建同业务值。

## 初始化 SQL

| 文件 | 表 |
| :--- | :--- |
| `docs/source/sql/init-auth.sql` | `user`、`user_bind`、`user_login_log`、`auth_token` |
| `docs/source/sql/init-tenant.sql` | `tenant_config` |
| `docs/source/sql/init-base-data.sql` | `base_tree_node`、`base_data` |
| `docs/source/sql/init-system-file.sql` | `system_file` |

## 认证数据

- `user` 是系统主账号表。
- `user_bind` 是第三方身份与系统主账号的绑定表。
- `user_login_log` 记录登录日志。
- `auth_token` 保存 token 会话状态，与 JWT 载荷中的 `tokenId` 对应。

## 租户数据

`tenant_config` 使用 key-value 模型：

- `config_code`：配置编码。
- `config_name`：配置名称。
- `config_value`：配置值。
- `config_remark`：说明。

租户公开信息、飞书告警、OSS STS、第三方扫码应用配置都从该表读取。

## 基础数据

`base_tree_node` 表达树形节点与业务类型，`base_data` 表达挂接在节点上的具体配置值。

关键约束：

- `biz_type` 区分业务场景。
- `node_key` 用于前后端交互。
- `data_bind_flag` 控制节点是否允许绑定数据。
- `FIELD_MGMT` 类型要求仅叶子节点可绑定数据。

## 文件数据

`system_file` 保存文件记录：

- 文件类型：`main_type`、`sub_type`。
- 业务归属：`master_id`。
- 存储信息：`url`、`file_key`、`endpoint`。
- 文件属性：`name`、`size`、`type`。

## 迁移建议

第一轮快照保留原始 SQL。新项目正式落地时建议把这些 SQL 转为 Flyway/Liquibase 迁移脚本，避免后续环境漂移。
