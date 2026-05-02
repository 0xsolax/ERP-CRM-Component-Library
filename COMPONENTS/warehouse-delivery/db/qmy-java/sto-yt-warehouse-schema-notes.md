# qmy-java sto/yt 仓储发货表结构说明

## 已识别表或模型

| 模型 | 表名 | 核心字段 | 说明 |
| :--- | :--- | :--- | :--- |
| `StoYtStore` | `sto_yt_store` | `productId`、`specificationId`、`realStore`、`enableStore`、`occupyStore`、`realTransit`、`enableTransit`、`occupyTransit`、`warningNumber`、`locationId` | 公共仓库存和在途库存 |
| `SalYtCustomerStore` | `sal_yt_customer_store` | `customerId`、`productId`、`specificationId`、`storeNumber`、`transitNumber`、`locationId`、`warningNumber` | 客户独立仓，来源归客户/订单边界 |
| `StoYtStoreRecord` | `sto_yt_store_record` | `storeId`、`specificationId`、`productId`、`purchaseId`、`orderSubId`、`type`、各库存初始值和变更值 | 出入库和库存变化流水 |
| `StoYtStoreOrder` | `sto_yt_store_order` | `code`、`type`、`productId`、`specificationId`、`supplierId`、`purchaseId`、`locationId`、`customerId`、`totalNumber`、`enterNumber` | 入库单/出入库单 |
| `StoYtStoreOrderOperation` | `sto_yt_store_order_operation` | `type`、`storeOrderId`、`number`、`operationDetail` | 入库单操作记录 |
| `StoYtBox` | `sto_yt_box` | `code`、`length`、`width`、`height`、`weight` | 打包箱规格 |
| `StoYtTransportCompany` | `sto_yt_transport_company` | `code`、`name`、`type`、`address`、`isHomeService` | 物流公司 |
| `StoYtDelivery` | `sto_yt_delivery` | `code`、`customerId`、`addressId`、`status`、`packageTime`、`deliveryTime`、`packageCode`、`transportCompanyId`、`deliveryAmount` | 发货单主表 |
| `StoYtDeliveryItem` | `sto_yt_delivery_item` | `deliveryId`、`orderSubId`、`orderSubItemId`、`productId`、`specificationId`、`locationId`、`number`、`shippedNumber` | 发货明细 |
| `StoYtDeliveryBox` | `sto_yt_delivery_box` | `deliveryId`、`boxCode`、`boxId`、`boxWeight`、`boxSize` | 发货包裹 |
| `StoYtDeliveryBoxItem` | `sto_yt_delivery_box_item` | `deliveryBoxId`、`specificationId`、`locationId`、`number`、`orderItemId`、`orderId`、`orderSubId` | 包裹明细 |
| `StoYtDeliveryReceive` | `sto_yt_delivery_receive` | `deliveryId`、`amount`、`currency`、`isCompletedReceive`、`receiveFinishTime` | 发货收款/运费相关记录 |
| `StoYtLocation` | 未见 `@TableName` | `id`、`name` | 库位模型，需确认实际表名 |

## 正式 DDL 待补齐

- 主键策略、租户字段、软删除字段、创建/更新审计字段。
- 库存表唯一键，需区分公共仓与客户独立仓。
- 发货单状态、包裹状态、入库单状态的枚举约束。
- 出入库流水不可变更策略。
- 库存变更事务隔离级别与并发扣减策略。
- 与订单、采购、客户、产品、财务模块的外键或逻辑关联。
