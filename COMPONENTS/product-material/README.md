# product-material 产品物料组件

## 定位

`product-material` 提供 ERP/CRM 制造与外贸项目中的产品资料、产品 BOM、伞架、面料、材料、包材和工序工价能力。它把产品主档、物料主档、基础字段、图片、成本字段和前端维护页面整理成可追溯快照，供新项目按行业差异改造接入。

本组件是可追溯快照，不是已打包 SDK。新项目应先确认行业对象、成本口径和报价边界，再按目标基座改造。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 业务组件 |
| 依赖组件 | `BASE/project-scaffold`、`base-data`、`file-oss`、`auth-permission` |
| 主要来源 | `RAW/PROJECTs/zhongsheng-backend`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-backend/` | 产品、材料、面料、包材、伞架、工序的 controller/service/manager/dao/entity/VO/DTO、错误码和权限常量 |
| `frontend/qmy-admin/` | 产品、工价、伞架、面料、材料、包材页面，API 封装和路由入口 |
| `db/zhongsheng-backend/` | 产品、产品 BOM、材料、面料、包材、伞架、工序 SQL |
| `docs/source/` | 来源说明与调研材料摘要 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照和接入验收清单 |

## 能力边界

已覆盖：

- 产品主档：产品编号、重量、体积、装柜预估、描述、总成本、售价。
- 产品组成：产品类型、伞架、材料、面料、印刷、包材、工序工价。
- 物料主档：伞架、面料、其他材料、包材、材料分类。
- 工序工价：工序维护、分页、列表、删除。
- 图片关联：产品、材料、伞架通过 `file-oss` 的 `system_file` 链路保存图片。
- 字段与分类：伞架、面料、包材、产品类型等依赖 `base-data` 的 `base_tree_node` 和 `base_data`。
- 前端页面：产品维护、产品表单、工价维护、伞架/面料/材料/包材维护。
- 前端路由权限：来源占位权限已在快照中替换为产品、材料、工序权限码。

待项目确认：

- 目标行业是否仍以伞类产品为中心，或需要兼容百货、机械、跨品类产品。
- `total_cost`、`selling_price` 是否仅作为产品主档参考价，报价单是否另行保存客户、数量、利润、汇率、税费等快照。
- 包材成本是按固定包材单价、人工录入，还是按纸箱长宽高和数量动态计算。
- 产品 BOM 是否允许临时手工项，还是必须绑定材料主档。
- 产品图片、材料图片是否公开访问，或需要签名 URL 与权限校验。

## 快速接入

1. 先接入 `BASE/project-scaffold`、`auth-permission`、`base-data`、`file-oss`。
2. 执行 `db/zhongsheng-backend/` 中产品、材料、面料、包材、伞架、工序 SQL。
3. 确认 `base-data` 中产品类型、面料种类/型号/门幅、包材类型、伞架功能/类型/长度/直径/骨数/材料等节点。
4. 接入后端 `product`、`material`、`process` 包及 DTO、错误码、权限常量。
5. 接入前端产品、工价、材料管理页面和 API。
6. 按目标项目权限系统映射 `product:*`、`material:*`、`process:*` 权限。
7. 对照 `docs/contracts/` 确认 API、表结构、权限码和成本边界。
8. 按 `docs/acceptance/ACCEPTANCE.md` 做产品 BOM、物料引用、图片、权限和报价边界验收。

## 安全与业务规则

- 价格、成本、报价敏感字段必须受权限控制；前端隐藏不等于安全边界。
- 产品主档成本不应覆盖报价单快照。报价单应保存客户、数量、利润、汇率、税费、包材调整等报价时点信息。
- `base_data` 的名称会被快照到产品/物料关系表，新项目需确认基础数据改名后的历史展示规则。
- 产品图片和物料图片依赖 `file-oss`，不得在产品组件里硬编码文件访问密钥。
- 来源存在 `/fabric/deteil` 拼写事实，接入新项目时应决定兼容旧路径或统一改为 `/fabric/detail`。
- 来源前端保留 `/packaging/typeList`、`/box-price/list` API 封装，但当前后端快照未找到对应 controller；已列为待验证缺口。
