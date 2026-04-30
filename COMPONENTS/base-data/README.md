# base-data 基础数据组件

## 定位

`base-data` 提供 ERP/CRM 后台通用基础数据能力：用 `base_tree_node` 维护业务分类树，用 `base_data` 维护挂在节点上的可编辑数据行，并通过 `nodeKey` 支撑字段管理、下拉选项、产品物料字段、包材/面料/伞架等业务模块复用。

本组件是可追溯快照，不是已打包 SDK。新项目应按目标基座改造后接入。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 基础组件 |
| 依赖组件 | `BASE/project-scaffold`、`auth-permission` |
| 主要来源 | `RAW/PROJECTs/project-scaffold`、`RAW/PROJECTs/zhongsheng-backend`、`RAW/PROJECTs/qmy-admin` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/project-scaffold/` | 通用基础数据基座实现：DTO、树节点种子、枚举、错误码、controller/service/manager/dao/entity/VO |
| `backend/zhongsheng-backend/` | 中圣业务版实现：权限注解、重复性校验、默认值保护、字段管理/物料用节点种子 |
| `frontend/qmy-admin/` | 字段管理页面、基础数据 API、字段下拉组件、路由入口 |
| `db/project-scaffold/` | 通用基础数据 DDL |
| `db/zhongsheng-backend/` | 中圣基础数据 DDL |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照与接入验收清单 |

## 能力边界

已覆盖：

- `base_tree_node` 基础树节点。
- `base_data` 通用数据行。
- `biz_type` 区分字段管理、面料、包材、伞架、产品等业务域。
- `node_key` 作为前后端稳定绑定键。
- `data_bind_flag` 控制节点是否允许挂数据。
- 叶子节点绑定约束。
- 基础数据新增、更新、删除、列表、按 nodeKey 查询、树节点列表。
- 字段管理前端：字段列维护、面料用量特殊行、可编辑下拉。
- 中圣版权限码：`base:data:save`、`base:data:remove`、`base:data:list`、`base:data:query`、`base:data:tree`。

待项目确认：

- 目标项目是否沿用 `FIELD_MGMT`、`FABRIC`、`PACKAGING`、`UMBRELLA_FRAME`、`PRODUCT` 这些 `biz_type`。
- 是否保留中圣版字段管理节点和默认值保护规则。
- 是否把产品、工价等同一路由下的页面拆到后续业务组件中独立授权。

已修正：

- 前端路由权限已由来源占位值 `sys:role:list` 替换为 `base:data:*`。
- `list` 空请求体按“不加节点过滤”处理，不再触发 `query == null` 空指针。
- `listByNodeKey` 对空请求体、空 `nodeKey`、未知 `nodeKey` 返回空列表，不再触发空指针。

## 快速接入

1. 执行 `db/` 中的 `base_tree_node`、`base_data` DDL。
2. 接入后端 DTO、枚举、初始化器、controller/service/manager/dao/entity/VO。
3. 根据项目业务维护 `BaseTreeNodeSeedEnum`，并确认 `BaseTreeBizTypeEnum.leafOnlyDataBind` 规则。
4. 接入 `auth-permission` 的方法级权限能力，或按目标项目替换权限注解。
5. 接入前端 `base-data.ts`、字段管理页面、`zs-label-select` 或 `field/components` 下拉组件。
6. 对照 `docs/contracts/` 确认 API、表结构、权限码。
7. 按 `docs/acceptance/ACCEPTANCE.md` 做树节点、数据行、空数据、越权和删除验收。

## 安全规则

- 不得把真实环境配置、数据库账号、token、密钥放入组件快照。
- 删除基础数据前必须确认引用关系；当前快照只做逻辑删除，业务引用校验需要目标项目补齐。
- 前端隐藏入口不是权限边界，后端接口必须同步校验。
- `nodeKey` 是装配稳定锚点，不应在上线后随意重命名。
