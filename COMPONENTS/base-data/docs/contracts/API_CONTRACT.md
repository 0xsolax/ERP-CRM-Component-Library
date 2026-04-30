# API_CONTRACT｜base-data

## 后端接口

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新基础数据 | POST | `/baseData/saveOrUpdate` | `BaseDataSaveDTO` | `Long` 记录 ID | `BASE_DATA_SAVE_OR_UPDATE` |
| 删除基础数据 | POST | `/baseData/delete` | `IdRequestParam` | `Boolean` | `BASE_DATA_DELETE` |
| 查询基础数据列表 | POST | `/baseData/list` | `BaseDataListQueryDTO` 可选 | `List<BaseDataVO>` | `BASE_DATA_LIST` |
| 按 nodeKey 查询 | POST | `/baseData/listByNodeKey` | `BaseDataQueryByNodeKeyDTO` | `List<BaseDataVO>` | `BASE_DATA_LIST_BY_NODE_KEY` |
| 查询树节点 | POST | `/baseData/treeNodeList` | `BaseTreeNodeListQueryDTO` 可选 | `List<BaseTreeNodeVO>` | `BASE_DATA_TREE_NODE_LIST` |

## 请求对象

### BaseDataSaveDTO

| 字段 | 说明 |
| :--- | :--- |
| `id` | 为空新增，不为空更新 |
| `nodeId` | 基础树节点 ID；新增必填，更新可不传 |
| `value1`、`value2`、`value3`、`value4` | 通用值槽 |
| `remark` | 备注 |
| `extJson` | 扩展 JSON |
| `isDeleted` | 来源前端用作逻辑删除标记 |

### BaseDataListQueryDTO

| 字段 | 说明 |
| :--- | :--- |
| `nodeIds` | 节点 ID 集合；为空时返回全部未删除基础数据 |

请求体可为空；空请求体等价于不传 `nodeIds`，返回全部未删除基础数据。

### BaseDataQueryByNodeKeyDTO

| 字段 | 说明 |
| :--- | :--- |
| `nodeKey` | 节点唯一标识，例如 `FIELD_MGMT_SIZE_RIB_COUNT` |

请求体可为空；空请求体、空字符串、空白字符串或未知 `nodeKey` 均返回空列表。

### BaseTreeNodeListQueryDTO

| 字段 | 说明 |
| :--- | :--- |
| `bizType` | 业务类型，例如 `FIELD_MGMT` |
| `nodeKey` | 中圣版支持按 nodeKey 过滤 |

## 前端 API 快照

| 方法 | 路径 | 来源 |
| :--- | :--- | :--- |
| `getBaseDataTreeNodeList` | `/baseData/treeNodeList` | `frontend/qmy-admin/src/api/zs/base-info/base-data.ts` |
| `getBaseDataList` | `/baseData/list` | 同上 |
| `saveOrUpdateBaseData` | `/baseData/saveOrUpdate` | 同上 |
| `deleteBaseData` | `/baseData/delete` | 同上 |
| `listByNodeKey` | `/baseData/listByNodeKey` | 同上 |
| `batchSaveOrUpdateBaseData` | `/baseData/batchSaveOrUpdate` | 前端封装存在，后端快照未覆盖 |

## 错误语义

| 场景 | 期望 |
| :--- | :--- |
| 节点不存在 | 返回基础树节点无效错误 |
| 节点不允许绑定数据 | 返回不允许绑定基础数据错误 |
| 同节点数据重复 | 返回基础数据重复错误 |
| 默认值被修改 | 返回默认数据不允许修改错误 |
| 删除不存在记录 | 返回删除失败或业务错误，目标项目需明确 |
