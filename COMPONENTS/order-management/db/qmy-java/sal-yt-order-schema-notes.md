# qmy-java 订单表结构说明

`RAW/PROJECTs/qmy-java` 未随源码提供完整订单建表 SQL。本文件按实体、Mapper XML 和前端字段整理接入时必须补齐的表结构，不作为可直接执行 DDL。

## 订单核心表

| 表 | 来源 | 关键字段 |
| :--- | :--- | :--- |
| `sal_yt_order` | `SalYtOrder.java`、`SalYtOrderMapper.xml` | `code`、`source_platform`、`platform_order_code`、`currency`、`customer_id`、`customer_address`、`receiver`、`receiver_phone`、`follow_employee_id`、`sale_employee_id`、`status`、`shipping_method`、`order_time`、`delivery_time`、`discount_amount`、`is_inbound_delivery`、`shipping_cost`、`receive_status`、`shipping_receive_status`、`exchange_rate`、`submit_order_time` |
| `sal_yt_order_sub` | `SalYtOrderSub.java`、`SalYtOrderSubMapper.xml` | `sub_code`、`order_id`、`order_type`、`receive_status`、`receive_shipping`、`payment_shipping` |
| `sal_yt_order_sub_item` | `SalYtOrderSubItem.java`、`SalYtOrderSubItemMapper.xml` | `order_sub_id`、`product_id`、`specification_id`、`price`、`number`、`label_id`、`status`、`supplier_id`、`supplier_price`、`occupy_store_number`、`occupy_transit_number`、`enter_number`、`delivery_number`、`apply_purchase_number`、`base_price` |
| `sal_yt_order_sub_item_confirm` | `SalYtOrderSubItemConfirm.java` | 半成品确认记录 |
| `sal_yt_order_sub_item_operation` | `SalYtOrderSubItemOperation.java` | 订单商品项操作记录，记录采购、退货、入库、发货、半成品确认、关闭等动作 |
| `sal_yt_order_sub_receive` | `SalYtOrderSubReceive.java` | 子订单回款记录 |
| `sal_yt_return_order` | `SalYtReturnOrder.java`、`SalYtReturnOrderMapper.xml` | 订单/采购单退货记录，`type = 1` 为订单，`type = 2` 为采购单 |

## 采购申请边界表

| 表 | 来源 | 关键字段 |
| :--- | :--- | :--- |
| `pur_yt_apply_purchase` | `PurYtApplyPurchase.java`、`PurYtApplyPurchaseMapper.xml` | `product_id`、`product_specification_id`、`supplier_id`、`sales_employee_id`、`customer_id`、`number`、`category_label_id`、`order_sub_id`、`order_sub_item_id`、`order_remark` |

## 引用表

| 引用表 | 用途 | 归属 |
| :--- | :--- | :--- |
| `sal_yt_customer`、客户地址 | 下单客户、收货地址、销售/跟进归属 | `customer-management` |
| 产品、规格、规格项、产品标签、供应商报价 | 订单商品项、采购申请、库存占用 | `product-material` / `purchase-supplier` |
| 库存、在途、入库、打包、发货包裹 | 入库发货、供应商发货、包裹详情 | `warehouse-delivery` |
| 回款、利润、财务流水 | 回款状态、利润状态、订单财务 | `finance` |
| `pur_yt_purchase`、`pur_yt_purchase_item` | 采购单和采购明细 | `purchase-supplier` |

## 接入注意

- qmy-java 表默认继承 `BaseEntity` 的创建/更新人、租户、软删除字段。
- 订单状态前端以 `-1/0/1/2/3/4/5/6/7` 表达，后端另有 `OrderStatusEnum` 的审核态命名；接入时必须统一状态语义。
- 订单子项状态是采购、入库、打包、发货、完成、关闭的核心驱动，不应只看父订单状态。
- 采购申请只是从订单生成采购需求的桥接，采购单、付款、入库和供应商流程应由采购组件承接。
