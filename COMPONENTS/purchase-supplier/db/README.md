# db｜purchase-supplier

## 文件说明

| 文件 | 来源 | 用途 |
| :--- | :--- | :--- |
| `zhongsheng-AI/init-supplier.sql` | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | legacy `supplier` 简版表结构 |
| `qmy-java/pur-yt-purchase-schema-notes.md` | qmy-java entity、Mapper XML、前端字段 | YT 复杂采购表结构说明，不是可直接执行 DDL |

## legacy 表

`supplier` 包含供应商编号、名称、联系人、电话、邮箱、地址、状态、备注和审计时间。

该层没有采购申请、采购单、采购明细、供应商联系人/跟进/标签、租户字段和软删除字段。

## YT 复杂采购表

复杂采购至少需要以下表族：

- `pur_yt_supplier`
- `pur_yt_supplier_follow`
- `pur_yt_apply_purchase`
- `pur_yt_purchase`
- `pur_yt_purchase_item`
- `pur_yt_purchase_follow`
- `pur_yt_purchase_payment`
- `pur_yt_store_warning`
- `pro_yt_product_specification_supplier`

## 迁移注意

- `supplier` 与 `pur_yt_supplier*` 是不同复杂度模型，不应混用。
- qmy-java 复杂表必须重新产出正式 DDL。
- 采购单编号、供应商、付款方式、入库状态、退货数量和订单来源都需要明确约束。
- 供应商规格对照依赖产品规格，不能脱离产品组件迁移。
