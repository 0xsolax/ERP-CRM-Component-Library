# COMP 产品物料基础数据

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 基础资料 |
| 复用等级 | 可参考改造 |
| 适用项目 | 制造、外贸、BOM、产品报价 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/zs/base-info`、`RAW/PROJECTs/qmy-admin/src/views/zs/material`、`RAW/PROJECTs/qmy-admin/src/api/zs`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material`、`RAW/PROJECTs/zhongsheng-backend/docs/sql`、`RAW/docs/zhongsheng` |

## 业务目标

维护产品、伞架、面料、材料、包材、工序、基础字段和分类，为报价、订单、采购和成本核算提供统一基础数据。

## 前端入口

- 产品：`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/product`。
- 字段/工序：`RAW/PROJECTs/qmy-admin/src/views/zs/base-info/field`、`process`。
- 物料：`RAW/PROJECTs/qmy-admin/src/views/zs/material/umbrella-frame`、`fabric`、`material`、`packaging`。
- API：`RAW/PROJECTs/qmy-admin/src/api/zs/product`、`src/api/zs/material`、`src/api/zs/base-info`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 产品保存/分页/详情/删除 | POST | `/product/saveOrUpdate`、`/product/page`、`/product/detail`、`/product/delete` | `ProductController` |
| 伞架管理 | POST | `/umbrellaFrame/saveOrUpdate`、`/page`、`/detail`、`/delete`、`/list` | `UmbrellaFrameController` |
| 面料管理 | POST | `/fabric/saveOrUpdate`、`/page`、`/list`、`/deteil`、`/delete` | `FabricController` |
| 材料管理 | POST | `/material/saveOrUpdate`、`/page`、`/delete`、`/category/list` | `MaterialController` |
| 包材管理 | POST | `/packaging/saveOrUpdate`、`/page`、`/delete`、`/typeList` | `PackagingController` |
| 工序管理 | POST | `/process/saveOrUpdate`、`/page`、`/list`、`/delete` | `ProcessController` |
| 基础数据 | POST | `/baseData/saveOrUpdate`、`/list`、`/listByNodeKey`、`/treeNodeList` | `BaseDataController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `product` | 产品编号、产品类型、图片、成本相关字段 | 产品主档 |
| `product_umbrella_frame`、`product_material`、`product_fabric`、`product_printing`、`product_packaging`、`product_process_price` | 产品与物料/工序/包材关系 | 产品 BOM 和成本组成 |
| `umbrella_frame`、`fabric`、`material`、`packaging`、`process` | 分类、规格、价格、图片、状态 | 基础物料与工价 |
| `base_tree_node`、`base_data` | `biz_type`、`node_id`、`value1/2/3`、`ext_json` | 通用基础数据 |

## 权限边界

- 新版后端已为产品、伞架、面料、材料、包材、工序和基础数据提供权限标识。
- 基础资料通常应区分查看、维护、删除；价格字段可能需要更高权限。

## 接入步骤

1. 先确认行业对象：伞类、百货、机械或其他产品。
2. 复用基础数据树时，先定义 `biz_type` 和节点是否允许挂数据。
3. 对齐产品 BOM 结构和成本计算所需字段。
4. 接入产品、材料、工序、包材列表与详情。
5. 与报价组件共享产品、成本和物料引用。

## 验收清单

- [ ] 产品能维护多类组成数据。
- [ ] 物料、伞架、面料、包材能被产品引用。
- [ ] 工序价格能进入成本计算。
- [ ] 基础数据可用于下拉、分类、字段配置。
- [ ] 图片或文件字段与上传组件对齐。

## 已知风险

- `FabricController` 现有路径里 `deteil` 拼写保留来源事实，接新项目时应统一契约。
- 产品 BOM 与成本公式强行业相关，需要在新项目需求阶段重新确认。

