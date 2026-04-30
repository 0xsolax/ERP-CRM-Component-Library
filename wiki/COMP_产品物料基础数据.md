# COMP 产品物料基础数据

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 业务基础资料 |
| 复用等级 | 可参考改造 |
| 适用项目 | 制造、外贸、BOM、产品报价、订单、采购 |
| 组件快照 | [COMPONENTS/product-material](../COMPONENTS/product-material/README.md) |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/zs/base-info`、`RAW/PROJECTs/qmy-admin/src/views/zs/material`、`RAW/PROJECTs/qmy-admin/src/api/zs`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/process`、`RAW/PROJECTs/zhongsheng-backend/docs/sql`、`RAW/docs/zhongsheng` |

## 组件快照

- [COMPONENTS/product-material](../COMPONENTS/product-material/README.md)
- [来源映射](../COMPONENTS/product-material/SOURCE_MAP.md)
- [组件规范](../COMPONENTS/product-material/docs/spec/COMPONENT_SPEC.md)
- [API 契约](../COMPONENTS/product-material/docs/contracts/API_CONTRACT.md)
- [数据契约](../COMPONENTS/product-material/docs/contracts/DATA_CONTRACT.md)
- [权限契约](../COMPONENTS/product-material/docs/contracts/PERMISSION_CONTRACT.md)
- [验收清单](../COMPONENTS/product-material/docs/acceptance/ACCEPTANCE.md)

## 业务目标

维护产品、伞架、面料、材料、包材、工序、基础字段和分类，为报价、订单、采购和成本核算提供统一产品物料基础。

产品物料组件不是报价组件。它提供默认成本、默认售价和组成明细；报价时的客户、数量、汇率、税费、利润、折扣和历史报价快照应由报价组件保存。

## 前端入口

- 产品：`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/product`。
- 工价：`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/process`。
- 物料：`RAW/PROJECTs/qmy-admin/src/views/zs/material/umbrella-frame`、`fabric`、`material`、`packaging`。
- API：`RAW/PROJECTs/qmy-admin/src/api/zs/product`、`src/api/zs/material`、`src/api/zs/base-info/process.ts`。
- 路由：`RAW/PROJECTs/qmy-admin/src/views/zs/router/async-modules/base-info.ts`、`material.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 产品保存/分页/详情/删除 | POST | `/product/saveOrUpdate`、`/product/page`、`/product/detail`、`/product/delete` | `ProductController` |
| 伞架管理 | POST | `/umbrellaFrame/saveOrUpdate`、`/page`、`/detail`、`/delete`、`/list` | `UmbrellaFrameController` |
| 面料管理 | POST | `/fabric/saveOrUpdate`、`/page`、`/list`、`/deteil`、`/detail`、`/delete` | `FabricController` |
| 材料管理 | POST | `/material/category/*`、`/material/saveOrUpdate`、`/material/page`、`/material/delete`、`/material/listByCategoryId` | `MaterialController` |
| 包材管理 | POST | `/packaging/saveOrUpdate`、`/packaging/saveOrUpdateDefaultPaperBox`、`/packaging/page`、`/packaging/delete` | `PackagingController` |
| 工序管理 | POST | `/process/saveOrUpdate`、`/process/page`、`/process/list`、`/process/delete` | `ProcessController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `product` | `product_code`、重量、体积、描述、`total_cost`、`selling_price` | 产品主档 |
| `product_type` | `product_id`、`type_id/name` | 产品类型快照 |
| `product_umbrella_frame`、`product_material`、`product_fabric`、`product_printing`、`product_packaging`、`product_process_price` | 产品与伞架、材料、面料、印刷、包材、工序关系 | 产品 BOM 和默认成本组成 |
| `umbrella_frame`、`fabric`、`material`、`packaging`、`process` | 分类、规格、价格、图片、状态 | 基础物料与工价 |
| `base_tree_node`、`base_data` | `biz_type`、`node_key`、`value1/2/3` | 字段分类和下拉数据，属于 `base-data` |
| `system_file` | `main_type`、`sub_type`、`master_id`、`url` | 产品、材料、伞架图片，属于 `file-oss` |

## 权限边界

- 产品：`product:product:save`、`product:product:page`、`product:product:detail`、`product:product:remove`。
- 材料：`material:material:*`、`material:category:*`。
- 面料：`material:fabric:*`。
- 伞架：`material:umbrella:*`。
- 包材：`material:packaging:*`。
- 工序：`process:process:*`。
- 价格和成本字段建议进一步区分查看、维护、删除权限。

## 接入步骤

1. 先接入 `BASE/project-scaffold`、`auth-permission`、`base-data`、`file-oss`。
2. 执行产品、材料、面料、包材、伞架、工序 SQL。
3. 初始化基础字段和分类节点。
4. 接入产品、物料、工序后端和前端页面。
5. 对齐权限码和菜单权限。
6. 以报价组件为下游，确认产品成本字段与报价快照字段的分工。

## 验收清单

- [ ] 产品能维护多类组成数据。
- [ ] 物料、伞架、面料、包材能被产品引用。
- [ ] 工序价格能进入产品默认成本。
- [ ] 基础数据可用于下拉、分类、字段配置。
- [ ] 图片或文件字段与 `file-oss` 对齐。
- [ ] 无权限用户不能访问后端接口。
- [ ] 报价单能复制产品和 BOM 快照，而不是只引用产品实时成本。

## 已知风险

- `FabricController` 来源路径里 `deteil` 拼写保留来源事实；快照已新增 `/fabric/detail` 标准路径并保留旧路径兼容。
- 来源前端曾存在 `/packaging/typeList` 和 `/box-price/list` 未使用封装；快照已移除，包材类型走 `base-data`，纸箱单价列表走 `/packaging/page` 加 `defaultTypeFlag = 1`。
- 产品 BOM 与成本公式强行业相关，需要在新项目需求阶段重新确认。
- 非伞类产品是否共表或分业务线，需要在客户需求阶段确认。
