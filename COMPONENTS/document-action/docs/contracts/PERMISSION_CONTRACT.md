# Permission Contract

| 权限 | 用途 |
| :--- | :--- |
| `document:action-log:list` | 查看动作日志 |
| `document:unlock:request` | 申请解锁和重新确认 |
| `document:unlock:approve` | 管理员解锁和兼容审批 |
| `document:owner:reassign` | 改派负责人 |
| `system:operation-log:page` | 系统操作日志页面 |

超级管理员拥有 `*` 时可访问全部公共单据动作。普通用户查询或重新确认时，后端必须按业务单据负责人或数据范围校验可见性。
