# qmy-java 报价表结构说明

`RAW/PROJECTs/qmy-java` 未随源码提供完整报价建表 SQL。本文件按实体、Mapper XML 和前端字段整理接入时必须补齐的表结构，不作为可直接执行 DDL。

## 主表

| 表 | 来源 | 关键字段 |
| :--- | :--- | :--- |
| `sal_sed_quotation` | `SalSedQuotation.java`、`SalSedQuotationMapper.xml` | `quotation_code`、`customer_id`、`salesman_id`、`procurement_cost`、`procurement_cost_state`、`logistics_cost`、`logistics_cost_state`、`total_cost`、`total_cost_state`、`order_amount`、`discount_amount`、`receive_address_id`、`receive_address`、`special_requirements`、`status`、`reject_reason`、`shift_status`、`currency`、`tax`、`fob`、`exw`、`exchange_rate` |
| `sal_sed_quotation_sku` | `SalSedQuotationSku.java`、`SalSedQuotationSkuMapper.xml` | `quotation_id`、`product_id`、`match_id`、`sku_id`、`quotation_price`、`quotation_base_price`、`quantity`、`volume`、`remark` |
| `sal_sed_quotation_sku_packing` | `SalSedQuotationSkuPacking.java`、`SalSedQuotationSkuPackingMapper.xml` | `quotation_id`、`quotation_sku_id`、`packing_id`、`attachment`、`box_mum`、`packing_num`、`cost`、`packing_size` |
| `sal_sed_quotation_history` | `SalSedQuotationHistory.java`、`SalSedQuotationHistoryMapper.xml` | `quotation_id`、`context`、`create_user`、`create_time`、`tenant_id`、`is_deleted` |

## 引用表

| 引用表 | 用途 | 归属 |
| :--- | :--- | :--- |
| `sal_yt_customer` | 报价客户、客户地址、客户跟进人、客户维度历史报价 | `customer-management` |
| `pro_sed_product`、`pro_sed_product_match`、`pro_sed_product_match_sku` | 产品、搭配、SKU、基础报价、体积、工艺量 | `product-material` |
| `pro_sed_fitting`、`pro_sed_fitting_part`、`pro_sed_packing`、`pro_sed_effect` | 配件、零件、包材、工艺效果成本 | `product-material` |
| `pro_sed_file` | SKU 图片、配件图片、总裁微信审核凭证 | `file-oss` |
| `sys_dictionary` | 币种、含税、FOB、EXW、油漆单价等字典配置 | `base-data` |
| `sal_sed_order`、`sal_sed_order_detail`、`sal_sed_order_operate_record` | 一键转订单、SKU 转订单、合并转订单 | `order-management` |

## 接入注意

- `qmy-java` 表默认继承 `BaseEntity` 的 `create_user`、`create_time`、`update_user`、`update_time`、`tenant_id`、`is_deleted` 等字段。
- 复杂报价流依赖软删除过滤，所有查询需保留 `is_deleted = 0` 口径。
- 历史报价、毛利率和均价/中位数计算依赖 `sal_sed_quotation_sku` 与报价主表币种、汇率快照。
- 转订单不应只复制报价主表，需要同步 `sal_sed_quotation_sku`、`sal_sed_quotation_sku_packing` 到订单明细。
