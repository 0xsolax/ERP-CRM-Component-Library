# COMP 基础数据

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | 基础设施 / 通用后台配置 |
| 复用等级 | 可参考改造 |
| 适用项目 | 多数 ERP/CRM 后台 |
| 来源路径 | `RAW/PROJECTs/project-scaffold/docs/sql/init-base-data.sql`、`RAW/PROJECTs/project-scaffold/project-core/src/main/java/com/qmy/project/core/base`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/base`、`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/field` |

## 组件快照

- [COMPONENTS/base-data](../COMPONENTS/base-data/README.md)
- [来源映射](../COMPONENTS/base-data/SOURCE_MAP.md)
- [组件规范](../COMPONENTS/base-data/docs/spec/COMPONENT_SPEC.md)
- [API 契约](../COMPONENTS/base-data/docs/contracts/API_CONTRACT.md)
- [数据契约](../COMPONENTS/base-data/docs/contracts/DATA_CONTRACT.md)
- [权限契约](../COMPONENTS/base-data/docs/contracts/PERMISSION_CONTRACT.md)
- [验收清单](../COMPONENTS/base-data/docs/acceptance/ACCEPTANCE.md)

## 业务目标

把字段、分类、下拉选项和业务字典收敛到统一的基础数据模型，后续产品、物料、报价、订单等模块可通过 `nodeKey` 和 `base_data.id` 复用同一套配置。

## 前端入口

- 字段管理页面：`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/field/index.vue`。
- API 封装：`RAW/PROJECTs/qmy-admin/src/api/zs/base-info/base-data.ts`。
- 可编辑下拉：`RAW/PROJECTs/qmy-admin/src/components/zs-label-select`。
- 路由入口：`RAW/PROJECTs/qmy-admin/src/views/zs/router/async-modules/base-info.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 保存或更新基础数据 | POST | `/baseData/saveOrUpdate` | `BaseDataController` |
| 删除基础数据 | POST | `/baseData/delete` | `BaseDataController` |
| 查询基础数据列表 | POST | `/baseData/list` | `BaseDataController` |
| 按 nodeKey 查询 | POST | `/baseData/listByNodeKey` | `BaseDataController` |
| 查询树节点 | POST | `/baseData/treeNodeList` | `BaseDataController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `base_tree_node` | `biz_type`、`parent_id`、`node_key`、`data_bind_flag` | 基础数据分类树和绑定规则 |
| `base_data` | `node_id`、`value1`-`value4`、`ext_json`、`is_deleted` | 挂在树节点下的数据行 |
| `BaseTreeNodeSeedEnum` | `nodeKey`、`bizType`、`parentSeed`、`dataBindFlag` | 启动时幂等初始化树节点 |
| `BaseTreeBizTypeEnum` | `value`、`leafOnlyDataBind` | 控制业务类型和叶子节点绑定规则 |

## 权限边界

- 中圣版后端使用 `base:data:save`、`base:data:remove`、`base:data:list`、`base:data:query`、`base:data:tree`。
- 字段管理是后台配置能力，不应开放给普通业务录入用户。
- 组件快照中的前端路由已使用 `base:data:list`、`base:data:save`，不再保留来源占位权限 `sys:role:list`。

## 接入步骤

1. 执行 `base_tree_node`、`base_data` DDL。
2. 按目标业务维护 `BaseTreeNodeSeedEnum`。
3. 接入后端 `BaseDataController` 与服务层。
4. 接入基础数据权限码。
5. 接入前端字段管理页面和可编辑下拉组件。
6. 用 `nodeKey` 对齐业务模块引用。

## 验收清单

- [ ] 树节点能按 `biz_type` 返回。
- [ ] `nodeKey` 能查询对应基础数据。
- [ ] 空请求体、空 `nodeKey`、未知 `nodeKey` 不触发空指针。
- [ ] 不允许在不可绑定节点上保存数据。
- [ ] 仅叶子绑定规则生效。
- [ ] 逻辑删除后列表不再返回数据。
- [ ] 无权限用户不能新增、编辑、删除基础数据。

## 已知风险

- 来源快照不是独立 SDK，需要按目标项目包名和权限框架整合。
- 业务模块引用 `base_data.id` 后，删除必须加引用保护。
