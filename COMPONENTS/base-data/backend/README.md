# backend

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `project-scaffold/` | 通用基座版基础数据实现 |
| `zhongsheng-backend/` | 中圣业务版基础数据实现 |

## 选择建议

- 新项目从空基座开始时，优先参考 `project-scaffold/` 的包结构和通用模型。
- 需要字段管理、面料、包材、产品等中圣业务节点时，参考 `zhongsheng-backend/` 的 `BaseTreeNodeSeedEnum`。
- 需要接口权限时，参考中圣版 `BaseDataController` 上的 `@PreAuthorize`，并与 `auth-permission` 的 SpEL 权限服务一起接入。

## 接入注意

- `BaseTreeNodeDataInitializer` 依赖启动时运行，必须保证 `base_tree_node` 表已经存在。
- `BaseDataServiceImpl.list` 来源代码声明请求体可空，但服务实现没有完全处理 `query == null`，接入时建议补空值防御。
- 删除当前是逻辑删除，业务引用校验需要按目标项目补齐。
