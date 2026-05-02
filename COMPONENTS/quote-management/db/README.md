# 数据库快照说明

## 文件

| 文件 | 来源 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-AI/init-quote.sql` | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | legacy `quote`、`quote_item` 表 |
| `qmy-java/sal-sed-quotation-schema-notes.md` | qmy-java entity、Mapper XML | 复杂报价流表结构说明，不是可执行 DDL |

## 表层次

- 基础 CRUD：`quote`、`quote_item`。
- 复杂流：`sal_sed_quotation`、`sal_sed_quotation_sku`、`sal_sed_quotation_sku_packing`、`sal_sed_quotation_history`。
- 转订单引用：`sal_sed_order`、`sal_sed_order_detail`、`sal_sed_order_operate_record`。

## 接入要求

- 目标项目应统一使用一种报价模型，不建议同时保留 legacy `quote` 与 SED `sal_sed_quotation` 两套生产表。
- 如果采用复杂流，必须补齐租户、软删除、创建/更新人、报价状态、审核状态和转订单状态字段。
- 历史报价、成本和毛利率涉及敏感商业信息，数据库查询必须配合客户/报价数据范围。
