# API Contract

| 接口 | 说明 | 权限 |
| :--- | :--- | :--- |
| `POST /document/actionLog/page` | 查询单据动作日志 | `DOCUMENT_ACTION_LOG_LIST` |
| `POST /document/unlock/request` | 申请解锁兼容入口 | `DOCUMENT_UNLOCK_REQUEST` |
| `POST /document/unlock/warning` | 管理员警告解锁 | `DOCUMENT_UNLOCK_APPROVE` |
| `POST /document/unlock/approve` | 审批同意兼容入口 | `DOCUMENT_UNLOCK_APPROVE` |
| `POST /document/unlock/reject` | 审批拒绝兼容入口 | `DOCUMENT_UNLOCK_APPROVE` |
| `POST /document/reconfirm` | 重新确认并锁定 | `DOCUMENT_UNLOCK_REQUEST` |
| `POST /document/owner/assign` | 管理员改派负责人 | `DOCUMENT_OWNER_REASSIGN` |
| `POST /system/operationLog/page` | 系统操作日志分页 | `SYSTEM_OPERATION_LOG_PAGE` |
