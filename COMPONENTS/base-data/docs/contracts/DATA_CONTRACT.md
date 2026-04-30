# DATA_CONTRACT｜base-data

## 表结构

### base_tree_node

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键 |
| `biz_type` | varchar(32) | 业务类型 |
| `parent_id` | bigint | 父节点，根节点为 `0` |
| `name` | varchar(255) | 节点名称 |
| `level` | int | 层级 |
| `sort_num` | int | 同级排序 |
| `node_key` | varchar(64) | 节点唯一标识 |
| `data_bind_flag` | tinyint | 是否允许绑定 `base_data` |
| `create_user`、`update_user` | bigint | 审计人 |
| `create_time`、`update_time` | datetime | 审计时间 |
| `is_deleted`、`deleted_time` | tinyint/datetime | 逻辑删除 |

约束：

- `node_key` 唯一。
- `biz_type + parent_id + name + is_deleted + deleted_time` 唯一。
- `parent_id`、`biz_type` 有索引。

### base_data

| 字段 | 类型 | 说明 |
| :--- | :--- | :--- |
| `id` | bigint | 主键 |
| `node_id` | bigint | 关联 `base_tree_node.id` |
| `value1` | varchar(512) | 主值 |
| `value2` | varchar(512) | 次值或标记 |
| `value3` | varchar(512) | 扩展值 |
| `value4` | varchar(512) | 扩展值 |
| `remark` | varchar(1024) | 备注 |
| `ext_json` | text | 扩展 JSON |
| `create_user`、`update_user` | bigint | 审计人 |
| `create_time`、`update_time` | datetime | 审计时间 |
| `is_deleted`、`deleted_time` | tinyint/datetime | 逻辑删除 |

约束：

- `node_id` 有索引。
- 重复性约束当前在服务层实现，不在数据库唯一键中实现。

## 节点种子

中圣版已包含以下业务域：

- `FIELD_MGMT`：字段管理。
- `FABRIC`：面料。
- `PACKAGING`：包材。
- `UMBRELLA_FRAME`：伞架。
- `PRODUCT`：产品。

典型 nodeKey：

- `FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH`
- `FIELD_MGMT_SIZE_MIDDLE_POLE_DIAMETER`
- `FIELD_MGMT_SIZE_RIB_COUNT`
- `FIELD_MGMT_FABRIC_USAGE`
- `FIELD_MGMT_UMBRELLA_FRAME_FUNCTION`
- `FABRIC_TYPE`
- `PACKAGING_TYPE`
- `PRODUCT_TYPE`

## 绑定规则

- 树节点负责分类和可绑定规则。
- 数据行只挂在允许绑定的节点下。
- `nodeKey` 用于查询和配置，不应作为展示名。
- 业务模块应保存 `base_data.id`，必要时同步保存名称快照，避免基础数据改名影响历史单据。

## 删除规则

- 来源实现使用逻辑删除。
- 目标项目应补业务引用检查，例如产品、物料、报价、订单已引用时不得删除。
- 默认值保护是业务规则，不是数据库规则。
