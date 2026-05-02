# qmy-java 采购表结构说明

`RAW/PROJECTs/qmy-java` 未随源码提供完整采购建表 SQL。本文件按实体、Mapper XML 和前端字段整理接入时必须补齐的表结构，不作为可直接执行 DDL。

## 采购核心表

| 表 | 来源 | 关键字段 |
| :--- | :--- | :--- |
| `pur_yt_supplier` | `PurYtSupplier.java`、`PurYtSupplierMapper.xml` | `code`、`name`、`short_name`、`address`、`remark` |
| `pur_yt_supplier_follow` | `PurYtSupplierFollow.java`、`PurYtSupplierFollowMapper.xml` | 供应商跟进记录 |
| `pur_yt_apply_purchase` | `PurYtApplyPurchase.java`、`PurYtApplyPurchaseMapper.xml` | `product_id`、`product_specification_id`、`supplier_id`、`sales_employee_id`、`customer_id`、`number`、`category_label_id`、`order_sub_id`、`order_sub_item_id`、`order_remark` |
| `pur_yt_purchase` | `PurYtPurchase.java`、`PurYtPurchaseMapper.xml` | `code`、`status`、`supplier_id`、`supplier_name`、`order_platform_code`、`shipping_cost`、`discount_amount`、`delivery_time`、`pay_method`、`pay_way`、`completed_time`、`submit_purchase_time`、`total_amount`、`wait_enter_number`、`total_payment_amount` |
| `pur_yt_purchase_item` | `PurYtPurchaseItem.java`、`PurYtPurchaseItemMapper.xml` | `purchase_id`、`apply_purchase_id`、`product_id`、`specification_id`、`order_sub_id`、`order_sub_item_id`、`supplier_price`、`number`、`enter_number`、`customer_id`、`sales_employee_id`、`status`、`delivery_number` |
| `pur_yt_purchase_follow` | `PurYtPurchaseFollow.java`、`PurYtPurchaseFollowMapper.xml` | 采购单跟进记录 |
| `pur_yt_purchase_payment` | `PurYtPurchasePayment.java`、`PurYtPurchasePaymentMapper.xml` | 采购付款记录，财务边界 |
| `pur_yt_store_warning` | `PurYtStoreWarning.java`、`PurYtStoreWarningMapper.xml` | `customer_id`、`product_id`、`specification_id`、`warning_time`、`warning_reason`、`store_name`、`is_apply_purchase`、库存/在途数量、`supplier_id`、`apply_purchase_number` |
| `pro_yt_product_specification_supplier` | `ProYtProductSpecificationSupplier.java`、Mapper XML | 产品规格与供应商规格/价格对照 |

## 引用对象

| 引用 | 用途 | 归属 |
| :--- | :--- | :--- |
| 产品、规格、规格项、图片 | 采购申请、采购单明细、供应商规格对照 | `product-material` |
| 订单、子订单、订单商品项 | 采购申请来源和采购追溯 | `order-management` |
| 客户、业务员 | 待采购、采购单明细筛选和归属 | `customer-management` / `auth-permission` |
| 库存、在途、入库 | 库存预警、采购入库、待入库数量 | `warehouse-delivery` |
| 付款、应付、对账 | 采购付款和财务核算 | `finance` |

## 接入注意

- qmy-java 表默认继承 `BaseEntity` 的创建/更新人、租户、软删除字段。
- `PurchaseStatusEnum` 与前端 `purchaseStatusList` 需统一。
- 采购申请不是采购单，生成采购单时必须保留申请来源。
- 采购退货要同时影响采购明细、订单/库存和财务，不能只写退货记录。
