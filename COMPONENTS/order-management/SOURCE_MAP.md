# SOURCE_MAP｜order-management

## 来源摘要

| 类型 | 路径 | 用途 | 处理方式 |
| :--- | :--- | :--- | :--- |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/OrdersController.java` | 简版订单 CRUD 入口 | 复制到 `backend/zhongsheng-AI/` |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Orders.java` | 简版订单主档对象 | 复制到 `backend/zhongsheng-AI/` |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service`、`mapper/OrdersMapper.java` | 简版分页、保存、删除分层 | 复制到 `backend/zhongsheng-AI/` |
| legacy SQL | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | `orders`、`order_item` 表 | 提取到 `db/zhongsheng-AI/init-order.sql` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtOrderController.java` | YT 订单主流程 API | 复制到 `backend/qmy-java/` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java` | 订单申请采购边界 API | 复制到 `backend/qmy-java/` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/yt` | 订单保存、审核、退货、发货、完结、关闭逻辑 | 复制到 `backend/qmy-java/` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/pur/yt/PurYtApplyPurchaseManager.java` | 采购申请处理 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/yt` | 订单、子订单、商品项、退货、操作记录对象 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pur/yt/PurYtApplyPurchase.java` | 采购申请桥接对象 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt` | 订单查询、聚合和状态数据证据 | 复制到 `backend/qmy-java/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/order` | 订单列表、新增、编辑、详情、申请采购、退货、发货、导出页面 | 复制到 `frontend/qmy-admin/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts` | 订单 API 封装 | 复制到 `frontend/qmy-admin/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/constant/yitang/sales.ts`、`finance.ts` | 订单状态、订单类型、发货方式、操作类型、回款状态枚举 | 复制到 `frontend/qmy-admin/` |
| 前端类型 | `RAW/PROJECTs/qmy-admin/src/interface/table.ts` | 订单列表 `ColumnProps` 类型 | 复制到 `frontend/qmy-admin/` |
| 前端依赖 API | `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`、`auth/org.ts`、`product/index.ts` | 订单表单依赖的客户、组织用户、产品数据 | 已裁剪为订单实际使用函数后复制 |
| 前端共享依赖 | `RAW/PROJECTs/qmy-admin/src/components/footer-actions/index.vue`、`components/product-selector`、`views/admin/store/modules/tags/index.ts` | 订单页面直接引用的通用组件和标签页 store | 不复制或不全量复制，列为前端基座/产品物料依赖 |
| 调研/PRD | `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`、`RAW/docs/zhongsheng` | 订单来源、采购、退货、完结、仓储/财务边界背景 | 复制到 `docs/source/` |

## 抽取范围

已抽取：

- `zhongsheng-AI` 简版 `orders/order_item` CRUD 后端和表结构。
- qmy-java YT 复杂订单 controller、service、manager、entity、param、VO、mapper、枚举。
- qmy-java 采购申请 controller/manager/entity/mapper，作为订单生成采购需求的边界证据。
- qmy-admin admin 订单页面、订单 API、订单状态常量、裁剪后的销售订单路由。
- qmy-admin 客户、组织、产品 API 的订单最小依赖函数：`getCustomerSelectList`、`getCustomerAddressList`、`getAllEmployee`、`getProductDetail`、`getCategoryLabelList`。
- qmy-admin `interface/table.ts`，用于订单列表表格类型。
- PRD 与调研记录，作为订单、采购、退货、完结和跨模块边界的业务证据。

未抽取：

- 完整采购单、供应商付款、采购入库、采购对账页面与后端；归 `purchase-supplier`。
- 仓库入库、打包、发货、包裹、物流详情完整实现；归 `warehouse-delivery`。
- 财务流水、应收、利润核算和收款单完整实现；归 `finance`。
- 客户维护、组织账号、产品维护等跨模块前端 API；本组件只保留订单页面直接调用的最小函数。
- qmy-admin 全局布局、权限守卫、下载工具、上传组件、通用表格等基座能力；归基座或通用组件。
- qmy-admin `footer-actions`、`product-selector`、`tagsStore` 等共享前端能力；本组件只记录为装配依赖，其中 `product-selector` 依赖产品/组合产品 API 和产品物料组件。
- qmy-java 完整 DDL；来源仓没有提供订单全量建表 SQL。

待验证：

- qmy-java 复杂订单各表在目标项目中的最终 DDL、索引、唯一键和软删除字段。
- 订单详情、删除、退货、发货、完结、关闭、导出是否需要补 `@RequiresDataPermissions` 与订单归属校验。
- `/sal/yt/order/delete`、`/confirmComplete` 等破坏性或状态变更接口是否需要从 GET 收口为 POST/DELETE。
- 订单从报价转入的直接接口在当前 YT 订单组件内没有独立封装，来源报价能力由 `quote-management` 承接。

## 清洗规则

- 未复制源项目嵌套 `.git/`、`.DS_Store`、构建产物、依赖目录和环境配置。
- 未复制 qmy-admin 销售管理中的客户、独立仓历史、报价页面路由，组件路由已裁剪到订单入口。
- 未把 `product-selector` 的递归产品维护依赖并入订单组件，避免将产品组合、图片选择、规格新增误收为订单私有能力。
- 采购、仓储、财务只保留订单直接调用或字段证据，不把对方完整模块并入订单组件。
- 保留源码包路径，便于后续按来源项目回查。
- 原始 `RAW/` 只读，不在抽取过程中修改。

## 事实与推断

### 已确认事实

- `zhongsheng-AI` 的 `OrdersController` 只提供 `/api/orders` 基础分页、详情、新增、编辑、删除。
- `zhongsheng-AI` 的 `init.sql` 有 `orders` 与 `order_item`，状态为 `pending/processing/completed/cancelled`。
- qmy-admin 订单 API 对应 `/sal/yt/order/*`，采购申请对应 `/pur/yt/applyPurchase/saveOrUpdate`。
- qmy-java 存在 `SalYtOrderController`、`SalYtOrderManager`、订单子表、退货表、操作记录、采购申请相关实现证据。
- qmy-java `SalYtOrderController` 只有 `/list` 明确标注 `@RequiresDataPermissions`，多个详情/退货/关闭/物流接口权限注解为空或注释。
- qmy-admin admin 销售订单路由原来源文件包含其他销售入口，本快照已裁剪为订单列表、新增、编辑、详情、申请采购。

### 推断

- YT 复杂订单以订单主表、子订单、商品项状态为核心，父订单状态不能单独代表履约状态。
- 采购申请是订单到采购模块的桥接，不应在订单组件内继续承接采购单付款、入库和供应商对账。
- 发货信息和包裹详情在订单页面展示，但真实物流和仓储动作应由仓储/发货组件提供。
- 回款状态、利润状态和金额展示属于订单可见信息，真实财务流水仍应由财务组件负责。

### 待验证问题

- 目标项目是否采用简版订单表，还是采用 YT 复杂订单表。
- 订单删除是否允许物理/逻辑删除，还是只允许关闭/作废。
- 报价转订单的字段快照、重复转换防护和幂等规则。
- 老板、部门负责人、业务员、跟单员对订单和子订单的可见/可操作范围。
