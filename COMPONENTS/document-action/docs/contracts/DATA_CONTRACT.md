# Data Contract

| 表 | 说明 |
| :--- | :--- |
| `document_action_log` | 单据动作日志 |
| `document_unlock_request` | 解锁申请和处理记录 |

关键字段：

- `document_type`：业务单据类型。
- `document_id / document_code`：单据 ID 和编号。
- `action_type`：动作类型。
- `before_status / after_status`：状态变化。
- `before_lock_state / after_lock_state`：锁定状态变化。
- `reason`：解锁或变更原因。
- `diff_summary / diff_detail_json`：差异摘要和明细。
- `operator_id / operator_name / action_time`：操作者和时间。

接入单据自身应维护锁定字段，并在状态变更前后调用日志写入。
