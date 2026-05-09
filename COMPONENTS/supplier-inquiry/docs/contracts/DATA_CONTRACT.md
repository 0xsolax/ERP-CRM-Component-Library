# Data Contract

核心表：`supplier_inquiry`

| 字段 | 说明 |
| :--- | :--- |
| `supplier_id / supplier_code / supplier_name` | 供应商主档和快照 |
| `target_type / target_id / target_code / target_name` | 询价对象类型、关联 ID 和快照 |
| `specification / unit` | 规格与单位快照 |
| `price / currency / tax_rate / moq / delivery_days` | 询价核心条件 |
| `quote_date / valid_until` | 报价日期和有效期 |
| `contact_name / contact_phone` | 本次询价联系人 |
| `owner_id / owner_name` | 录入人 |
| `modification_log_json` | 字段级编辑日志 |

逻辑删除使用 `is_deleted` 和 `deleted_time`。
