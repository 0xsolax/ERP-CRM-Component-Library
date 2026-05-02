# DATA_CONTRACT｜purchase-supplier

## 数据对象

| 表/对象 | 来源 | 说明 |
| :--- | :--- | :--- |
| `supplier` | `zhongsheng-AI/init.sql`、`Supplier.java` | legacy 供应商主档 |
| `pur_yt_supplier` | `PurYtSupplier.java`、Mapper XML | YT 供应商主档 |
| `pur_yt_supplier_follow` | `PurYtSupplierFollow.java` | 供应商跟进 |
| `pro_yt_product_specification_supplier` | `ProYtProductSpecificationSupplier.java` | 产品规格与供应商规格对照 |
| `pur_yt_apply_purchase` | `PurYtApplyPurchase.java` | 待采购申请 |
| `pur_yt_purchase` | `PurYtPurchase.java` | 采购单主表 |
| `pur_yt_purchase_item` | `PurYtPurchaseItem.java` | 采购单明细 |
| `pur_yt_purchase_follow` | `PurYtPurchaseFollow.java` | 采购单跟进 |
| `pur_yt_purchase_payment` | `PurYtPurchasePayment.java` | 采购付款，财务边界 |
| `pur_yt_store_warning` | `PurYtStoreWarning.java` | 库存预警 |

## 核心字段

| 对象 | 字段 | 说明 |
| :--- | :--- | :--- |
| `supplier` | `code`、`name`、`contact`、`phone`、`email`、`address`、`status` | 简版供应商主档 |
| `pur_yt_supplier` | `code`、`name`、`shortName`、`address`、`remark` | 复杂供应商主档 |
| `pur_yt_apply_purchase` | `productId`、`productSpecificationId`、`supplierId`、`salesEmployeeId`、`customerId`、`number`、`orderSubId`、`orderSubItemId` | 订单/库存预警产生的采购需求 |
| `pur_yt_purchase` | `code`、`status`、`supplierId`、`supplierName`、`shippingCost`、`discountAmount`、`deliveryTime`、`payMethod`、`payWay`、`totalAmount` | 采购单主档 |
| `pur_yt_purchase_item` | `purchaseId`、`applyPurchaseId`、`productId`、`specificationId`、`orderSubItemId`、`supplierPrice`、`number`、`enterNumber`、`customerId`、`status` | 采购单明细和来源追溯 |
| `pur_yt_store_warning` | `customerId`、`productId`、`specificationId`、`warningTime`、`warningReason`、库存/在途数量、`supplierId`、`applyPurchaseNumber` | 库存预警申购 |

## 状态与枚举

| 字段 | 值 | 含义 | 来源 |
| :--- | :--- | :--- | :--- |
| `supplier.status` | `1` | 启用 | legacy SQL |
| `supplier.status` | `0` | 停用 | legacy SQL |
| `purchaseStatusList` | `0` | 暂存 | `constant/yitang/purchase.ts` |
| `purchaseStatusList` | `1` | 采购中 | `constant/yitang/purchase.ts` |
| `purchaseStatusList` | `2` | 已入库 | `constant/yitang/purchase.ts` |
| `purchaseStatusList` | `3` | 已发货 | `constant/yitang/purchase.ts` |
| `paymentMethodList` | `0` | 群内支付 | `constant/yitang/purchase.ts` |
| `paymentMethodList` | `1` | 付款码 | `constant/yitang/purchase.ts` |
| `paymentMethodList` | `2` | 1688 | `constant/yitang/purchase.ts` |
| `semiFinishedStatusList` | `0` | 待确认 | `constant/yitang/purchase.ts` |
| `semiFinishedStatusList` | `1` | 已确认 | `constant/yitang/purchase.ts` |

## 关系

| 来源对象 | 关系 | 目标对象 | 说明 |
| :--- | :--- | :--- | :--- |
| `pur_yt_apply_purchase.supplier_id` | 多对一 | `pur_yt_supplier` | 待采购供应商 |
| `pur_yt_apply_purchase.order_sub_item_id` | 多对一 | 订单商品项 | 采购来源 |
| `pur_yt_purchase.supplier_id` | 多对一 | `pur_yt_supplier` | 采购单按供应商聚合 |
| `pur_yt_purchase_item.purchase_id` | 多对一 | `pur_yt_purchase` | 采购明细归属 |
| `pur_yt_purchase_item.apply_purchase_id` | 多对一 | `pur_yt_apply_purchase` | 采购明细来源 |
| `pro_yt_product_specification_supplier.supplier_id` | 多对一 | `pur_yt_supplier` | 供应商规格对照 |
| `pur_yt_store_warning.product_id/specification_id` | 多对一 | 产品/规格 | 库存预警对象 |

## 约束

- 唯一键：建议 `tenant_id + supplier.code`、`tenant_id + purchase.code` 唯一。
- 逻辑删除：qmy-java 复杂流继承 `BaseEntity`，正式 DDL 应包含租户和软删除字段。
- 审计字段：采购申请、采购单、退货、通知供应商、撤回、导出都应记录操作人和操作时间。
- 多租户字段：legacy `supplier` 无租户字段，接入时必须补。

## 迁移注意

- 简版 `supplier` 不包含联系人、跟进、标签和供应商规格对照。
- 采购申请、采购单、采购明细、库存预警是独立对象，不能只用一个采购表承接。
- 采购单退货和付款会影响仓储与财务，需跨组件事务或事件设计。
