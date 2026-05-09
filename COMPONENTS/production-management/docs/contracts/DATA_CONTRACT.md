# Data Contract

| 表 | 说明 |
| :--- | :--- |
| `production_group` | 生产组主档 |
| `production_order` | 生产总单 |
| `production_order_progress` | 产品行生产进度 |
| `production_order_batch` | 分批安排 |

关键字段：

- `production_order.order_type`：`master` 来源订单总单，`standalone` 手工生产单。
- `master_order_key`：来源订单或手工单稳定键。
- `production_order_progress.line_key`：订单产品行键。
- `order_qty / purchased_qty / inbound_qty / planned_qty / delivered_qty`：产品行进度核心数量。
- `lock_state / needs_reconfirm / reconfirm_scope_json`：公共单据动作字段。
