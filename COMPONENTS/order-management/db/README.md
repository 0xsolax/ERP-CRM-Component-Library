# db｜order-management

## 文件说明

| 文件 | 来源 | 用途 |
| :--- | :--- | :--- |
| `zhongsheng-AI/init-order.sql` | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | legacy `orders`、`order_item` 简版表结构 |
| `qmy-java/sal-yt-order-schema-notes.md` | qmy-java entity、Mapper XML、前端字段 | YT 复杂订单表结构说明，不是可直接执行 DDL |

## legacy 表

`orders` 是订单主表，包含订单编号、客户、来源报价、下单日期、交期、总金额、状态、备注和审计时间。

`order_item` 是订单明细，包含订单 ID、产品、产品名称、数量、单价、金额和备注。

该层没有租户字段、软删除字段、审核字段、采购/仓储/财务状态，适合简版订单原型或迁移参考。

## YT 复杂表

qmy-java 订单至少需要以下表族：

- `sal_yt_order`
- `sal_yt_order_sub`
- `sal_yt_order_sub_item`
- `sal_yt_order_sub_item_confirm`
- `sal_yt_order_sub_item_operation`
- `sal_yt_order_sub_receive`
- `sal_yt_return_order`
- `pur_yt_apply_purchase`

复杂表需要与客户、产品规格、供应商、采购单、库存、发货、财务回款表建立外键或逻辑引用。

## 迁移注意

- `zhongsheng-AI/init-order.sql` 可作为简版 schema 片段使用，但目标项目仍应补租户、软删除、索引和唯一约束。
- qmy-java 复杂表必须重新产出正式 DDL，不能只从实体反推上线。
- 状态字段必须统一前后端语义，尤其是父订单状态、子订单状态、商品项状态和审核态。
- 订单编号、子订单编号、平台订单号应明确租户内唯一规则。
- 订单金额、折扣、运费、汇率、关闭金额和回款字段要与财务组件确认精度和来源。
