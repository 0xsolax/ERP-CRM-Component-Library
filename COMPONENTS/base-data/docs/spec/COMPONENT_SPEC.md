# COMPONENT_SPEC｜base-data

## 目标

为 ERP/CRM 后台提供统一基础数据模型，使字段、分类、下拉选项和业务字典可以通过同一套树节点与数据行维护，减少新项目重复建表和重复写页面。

## 后端规范

### 树节点

- 表名：`base_tree_node`。
- `biz_type` 表示业务域，例如 `FIELD_MGMT`、`FABRIC`、`PACKAGING`、`UMBRELLA_FRAME`、`PRODUCT`。
- `parent_id` 表示树结构，根节点为 `0`。
- `node_key` 是稳定 key，用于前后端按节点查询。
- `data_bind_flag = 1` 表示该节点允许挂 `base_data`。
- 仅叶子节点可绑定时，由 `BaseTreeBizTypeEnum.leafOnlyDataBind` 和服务层共同校验。

### 数据行

- 表名：`base_data`。
- `node_id` 指向 `base_tree_node.id`。
- `value1` 到 `value4` 是通用值槽。
- `ext_json` 保存扩展结构。
- 删除使用 `is_deleted` 与 `deleted_time`，默认不物理删除。

### 初始化

- `BaseTreeNodeSeedEnum` 定义节点种子。
- `BaseTreeNodeDataInitializer` 在应用启动时按 `nodeKey` 幂等插入或补齐节点。
- SQL 只建表，不写死种子主键。

### 服务规则

- 新增时必须传 `nodeId`。
- 更新时未传 `nodeId`，默认保留原节点。
- 节点不存在、节点不允许绑定、非叶子节点绑定数据都应报错。
- `list` 允许空请求体，按“不加节点过滤”查询未删除数据。
- `listByNodeKey` 对空请求体、空 `nodeKey`、未知 `nodeKey` 返回空列表，用于下拉组件的空态兜底。
- 中圣版额外校验：
  - 普通节点同一 `nodeId` 下 `value1` 不重复。
  - 面料用量节点按 `value1 + value2` 防重。
  - 默认印刷方式、默认包材类型不允许修改。

## 前端规范

- API 统一走 `/baseData/*`。
- 字段管理页面用 `treeNodeList({ bizType: 'FIELD_MGMT' })` 拉取树。
- 二级节点作为 Tab，子节点作为字段列。
- 普通字段新增、编辑、删除通过 `saveOrUpdate`。
- 面料用量作为特殊节点，使用多值槽表达组合数据。
- 可编辑下拉组件通过 `nodeKey` 或 `nodeId` 拉取并维护基础数据。

## 装配顺序

1. 执行 DDL。
2. 引入后端 DTO、枚举、初始化器、controller/service/manager/dao/entity/VO。
3. 配置目标项目的 `BaseTreeNodeSeedEnum`。
4. 接入权限注解和权限码。
5. 接入前端 API、字段管理页面、下拉组件、路由。
6. 做树节点、数据行、权限、删除、空数据验收。

## 已知缺口

- 快照不是一个独立可编译模块，`project-scaffold` 和 `zhongsheng-backend` 包名不同。
- 来源前端保留 `batchSaveOrUpdate` API 封装，但中圣 controller 未提供同名接口。
