# DATA_CONTRACT｜order-management

## 数据对象

| 表/对象 | 来源 | 说明 |
| :--- | :--- | :--- |
| `orders` | `zhongsheng-AI/init.sql`、`Orders.java` | legacy 订单主表 |
| `order_item` | `zhongsheng-AI/init.sql` | legacy 订单明细表 |
| `sal_yt_order` | `SalYtOrder.java`、`SalYtOrderMapper.xml` | YT 复杂订单主表 |
| `sal_yt_order_sub` | `SalYtOrderSub.java`、`SalYtOrderSubMapper.xml` | 子订单，区分成品/半成品等订单类型 |
| `sal_yt_order_sub_item` | `SalYtOrderSubItem.java`、`SalYtOrderSubItemMapper.xml` | 子订单商品项，驱动采购、入库、发货、退货状态 |
| `sal_yt_order_sub_item_confirm` | `SalYtOrderSubItemConfirm.java` | 半成品确认记录 |
| `sal_yt_order_sub_item_operation` | `SalYtOrderSubItemOperation.java`、Mapper XML | 商品项操作记录 |
| `sal_yt_order_sub_receive` | `SalYtOrderSubReceive.java` | 子订单回款记录 |
| `sal_yt_return_order` | `SalYtReturnOrder.java`、Mapper XML | 订单/采购退货记录 |
| `pur_yt_apply_purchase` | `PurYtApplyPurchase.java`、Mapper XML | 订单商品项生成采购申请的桥接表 |

## 核心字段

| 对象 | 字段 | 说明 | 来源 |
| :--- | :--- | :--- | :--- |
| `orders` | `code`、`customer_id`、`quote_id`、`order_date`、`delivery_date`、`total_amount`、`status` | 简版订单编号、客户、来源报价、交期、金额、状态 | `init-order.sql` |
| `order_item` | `order_id`、`product_id`、`product_name`、`quantity`、`unit_price`、`amount` | 简版订单明细 | `init-order.sql` |
| `sal_yt_order` | `code`、`source_platform`、`platform_order_code`、`currency`、`customer_id` | 订单编号、来源平台、平台单号、币种、客户 | `SalYtOrder.java` |
| `sal_yt_order` | `customer_address_id`、`customer_address`、`receiver`、`receiver_phone` | 收货地址和收货人 | `SalYtOrder.java` |
| `sal_yt_order` | `follow_employee_id`、`sale_employee_id`、`follow_ratio`、`sale_ratio` | 跟单员、业务员和比例 | `SalYtOrder.java` |
| `sal_yt_order` | `status`、`audit_opinion`、`shipping_method`、`order_time`、`delivery_time` | 状态、审核意见、发货方式、下单和交货时间 | `SalYtOrder.java` |
| `sal_yt_order` | `discount_amount`、`shipping_cost`、`exchange_rate`、`end_amount`、`end_other_amount` | 折扣、运费、汇率、关闭金额 | `SalYtOrder.java` |
| `sal_yt_order` | `receive_status`、`shipping_receive_status`、`order_finish_time` | 回款和完结状态 | `SalYtOrder.java` |
| `sal_yt_order_sub` | `sub_code`、`order_id`、`order_type`、`receive_status` | 子订单编号、父订单、订单类型、回款状态 | `SalYtOrderSub.java` |
| `sal_yt_order_sub_item` | `product_id`、`specification_id`、`price`、`number`、`label_id` | 商品、规格、价格、数量、标签 | `SalYtOrderSubItem.java` |
| `sal_yt_order_sub_item` | `status`、`supplier_id`、`supplier_price`、`base_price` | 商品项状态、供应商、采购价、基准价 | `SalYtOrderSubItem.java` |
| `sal_yt_order_sub_item` | `occupy_store_number`、`occupy_transit_number`、`enter_number`、`delivery_number`、`apply_purchase_number` | 库存/在途占用、入库、发货、采购数量 | `SalYtOrderSubItem.java` |
| `sal_yt_return_order` | `type`、`order_sub_item_id`、`purchase_item_id`、`return_number`、`reason` | 退货类型、关联项、退货数量、原因 | `SalYtReturnOrder.java` |
| `pur_yt_apply_purchase` | `product_id`、`product_specification_id`、`supplier_id`、`customer_id`、`number`、`order_sub_id`、`order_sub_item_id` | 订单商品项生成采购申请 | `PurYtApplyPurchase.java` |

## 状态与枚举

| 字段 | 值 | 含义 | 来源 |
| :--- | :--- | :--- | :--- |
| `orders.status` | `pending` | 待处理 | legacy SQL |
| `orders.status` | `processing` | 生产中 | legacy SQL |
| `orders.status` | `completed` | 已完成 | legacy SQL |
| `orders.status` | `cancelled` | 已取消 | legacy SQL |
| `orderStatusList` | `-1` | 暂存 | `constant/yitang/sales.ts` |
| `orderStatusList` | `0` | 待采购 | `constant/yitang/sales.ts` |
| `orderStatusList` | `1` | 待入库 | `constant/yitang/sales.ts` |
| `orderStatusList` | `2` | 待打包 | `constant/yitang/sales.ts` |
| `orderStatusList` | `3` | 待发货 | `constant/yitang/sales.ts` |
| `orderStatusList` | `4` | 已发货 | `constant/yitang/sales.ts` |
| `orderStatusList` | `5` | 已完成 | `constant/yitang/sales.ts` |
| `orderStatusList` | `6` | 待确认 | `constant/yitang/sales.ts` |
| `orderStatusList` | `7` | 已关闭 | `constant/yitang/sales.ts` |
| `orderTypeList` | `1` | 成品单 | `constant/yitang/sales.ts` |
| `orderTypeList` | `2` | 半成品单 | `constant/yitang/sales.ts` |
| `shippingMethodList` | `1` | 整单齐发 | `constant/yitang/sales.ts` |
| `shippingMethodList` | `2` | 单款齐发 | `constant/yitang/sales.ts` |
| `shippingMethodList` | `3` | 单规格齐发 | `constant/yitang/sales.ts` |
| `shippingMethodList` | `4` | 有货就发 | `constant/yitang/sales.ts` |
| `operationTypeList` | `1` | 下单 | `constant/yitang/sales.ts` |
| `operationTypeList` | `2` | 新增采购单 | `constant/yitang/sales.ts` |
| `operationTypeList` | `3` | 订单退货 | `constant/yitang/sales.ts` |
| `operationTypeList` | `4` | 入库 | `constant/yitang/sales.ts` |
| `operationTypeList` | `5` | 发货 | `constant/yitang/sales.ts` |
| `operationTypeList` | `6` | 半成品确认规格 | `constant/yitang/sales.ts` |
| `operationTypeList` | `7` | 采购单退货 | `constant/yitang/sales.ts` |
| `operationTypeList` | `8` | 关闭订单 | `constant/yitang/sales.ts` |
| `ReturnOrderTypeEnum` | `1` | 订单退货 | qmy-java enum/entity |
| `ReturnOrderTypeEnum` | `2` | 采购单退货 | qmy-java enum/entity |

说明：qmy-java 还存在 `OrderStatusEnum` 的审核态命名，与前端 `orderStatusList` 不是同一语义层；接入时必须统一状态模型。

## 关系

| 来源对象 | 关系 | 目标对象 | 说明 |
| :--- | :--- | :--- | :--- |
| `orders.customer_id` | 多对一 | 客户 | legacy 客户引用 |
| `orders.quote_id` | 多对一 | 报价 | legacy 来源报价引用 |
| `order_item.order_id` | 多对一 | `orders.id` | legacy 明细归属 |
| `sal_yt_order.customer_id` | 多对一 | `sal_yt_customer` | 复杂订单客户归属 |
| `sal_yt_order_sub.order_id` | 多对一 | `sal_yt_order.id` | 子订单归属 |
| `sal_yt_order_sub_item.order_sub_id` | 多对一 | `sal_yt_order_sub.id` | 商品项归属 |
| `sal_yt_order_sub_item.product_id/specification_id` | 多对一 | 产品/规格 | 产品物料依赖 |
| `sal_yt_return_order.order_sub_item_id` | 多对一 | `sal_yt_order_sub_item.id` | 订单退货归属 |
| `pur_yt_apply_purchase.order_sub_item_id` | 多对一 | `sal_yt_order_sub_item.id` | 采购申请来源 |
| `sal_yt_order_sub_item_operation` | 多对一 | 商品项/订单 | 操作记录 |

## 约束

- 唯一键：建议 `tenant_id + code`、`tenant_id + sub_code` 唯一；平台单号按业务确认是否唯一。
- 逻辑删除：qmy-java 复杂流继承 `BaseEntity`，正式 DDL 应包含租户、创建/更新、删除标记。
- 审计字段：订单写接口、退货、发货、关闭、采购申请都应记录操作人和操作时间。
- 排序字段：列表默认按创建时间或订单时间倒序。
- 多租户字段：复杂订单必须继承租户字段；legacy SQL 无租户字段，接入时要补。

## 迁移注意

- `orders/order_item` 与 `sal_yt_order*` 是两套不同复杂度模型，不应混用。
- qmy-java 复杂表需要目标项目重新设计正式 DDL、索引、外键/逻辑引用和状态迁移脚本。
- 报价转订单必须冻结报价时点的客户、商品、价格、汇率、成本等字段，不能后续完全依赖实时主数据。
- 退货、关闭、发货会改变数量和金额，必须以操作记录支持审计。
