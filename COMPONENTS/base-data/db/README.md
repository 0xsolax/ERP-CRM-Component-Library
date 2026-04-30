# db

## 文件

| 文件 | 说明 |
| :--- | :--- |
| `project-scaffold/init-base-data.sql` | 通用基础数据 DDL |
| `zhongsheng-backend/init-base-data.sql` | 中圣基础数据 DDL |

## 表关系

```text
base_tree_node.id
  ↓
base_data.node_id
```

## 核心字段

- `base_tree_node.biz_type`：业务类型。
- `base_tree_node.node_key`：前后端绑定 key。
- `base_tree_node.data_bind_flag`：是否允许挂基础数据。
- `base_data.value1` 到 `value4`：通用值槽。
- `base_data.ext_json`：扩展 JSON。
- `is_deleted`、`deleted_time`：逻辑删除。

## 接入注意

- 种子节点不在 SQL 中写死主键，由 `BaseTreeNodeDataInitializer` 幂等插入。
- `node_key` 有唯一约束，新项目上线后应避免随意改名。
- 业务模块引用 `base_data.id` 时，删除前应做引用保护。
