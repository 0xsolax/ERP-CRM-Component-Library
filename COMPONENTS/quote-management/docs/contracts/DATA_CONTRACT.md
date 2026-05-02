# DATA_CONTRACT｜quote-management

## 表模型选择

| 模型 | 表 | 适用 |
| :--- | :--- | :--- |
| 基础 CRUD | `quote`、`quote_item` | 简单报价单，只有客户、日期、总金额、明细 |
| 复杂流 | `sal_sed_quotation`、`sal_sed_quotation_sku`、`sal_sed_quotation_sku_packing`、`sal_sed_quotation_history` | 成本、审核、历史报价、转订单 |

生产接入应选择一种主模型。复杂流不建议继续使用 legacy `quote` 表。

## 复杂流主字段

### `sal_sed_quotation`

| 字段 | 含义 |
| :--- | :--- |
| `quotation_code` | 报价单编号 |
| `customer_id` | 客户 ID |
| `salesman_id` | 业务员 ID |
| `procurement_cost`、`procurement_cost_state` | 采购成本和确认状态 |
| `logistics_cost`、`logistics_cost_state` | 物流成本和确认状态 |
| `total_cost`、`total_cost_state` | 总成本和确认状态 |
| `order_amount` | 订单金额 |
| `discount_amount` | 优惠金额 |
| `receive_address_id`、`receive_address` | 收货地址快照 |
| `special_requirements` | 特殊要求 |
| `status` | 报价状态 |
| `reject_reason` | 驳回原因 |
| `shift_status` | 转订单状态 |
| `currency`、`tax`、`fob`、`exw`、`exchange_rate` | 币种、税、贸易条款和汇率快照 |

### `sal_sed_quotation_sku`

| 字段 | 含义 |
| :--- | :--- |
| `quotation_id` | 报价单 ID |
| `product_id` | 产品 ID |
| `match_id` | 产品搭配 ID |
| `sku_id` | SKU ID |
| `quotation_price` | 最终报价 |
| `quotation_base_price` | 基础报价快照 |
| `quantity` | 数量 |
| `volume` | 体积 |
| `remark` | 备注 |

### `sal_sed_quotation_sku_packing`

| 字段 | 含义 |
| :--- | :--- |
| `quotation_id` | 报价单 ID |
| `quotation_sku_id` | 报价 SKU ID |
| `packing_id` | 包材 ID |
| `attachment` | 附件 |
| `box_mum` | 所需包材数量 |
| `packing_num` | 装箱数 |
| `cost` | 包材成本 |
| `packing_size` | 包材尺寸 |

### `sal_sed_quotation_history`

| 字段 | 含义 |
| :--- | :--- |
| `quotation_id` | 报价单 ID |
| `context` | 操作类型 |
| `create_user` | 操作人 |
| `create_time` | 操作时间 |

## 字典和状态

- 报价状态来源：`QuotationStatusEnum` 与 `frontend/qmy-admin/src/constant/sed/quotation.ts`。
- 操作记录来源：`QuotationOperationEnum` 与 `quotationOperationList`。
- 会签动作来源：`QuotationJointAuditActionEnum`。
- 币种、含税、FOB、EXW 来源：`base-data` 或前端 `constant/sed/sales.ts`。

## 数据一致性

- 报价主表、SKU、包材、历史必须共享 `tenant_id`、`is_deleted` 和创建人口径。
- 删除报价或重新创建报价时，不得破坏已转订单的历史快照。
- 转订单后应记录报价来源，避免订单明细失去报价追溯。
- 历史报价统计不能使用被删除报价或被删除 SKU。
- 美元报价参与历史均价/中位数时必须使用报价快照汇率，不使用当前汇率覆盖历史。
