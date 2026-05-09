# Review Evidence

## 已确认

- `GUIDE_公共单据动作.md` 覆盖状态、接口、数据表、前端交互和边界。
- 来源代码中存在 `DocumentActionController`、`DocumentActionServiceImpl`、日志 DAO/Manager 和差异工具。
- 前端存在公共日志抽屉和系统操作日志页面。
- 报价、订单、采购、生产 GUIDE 均已引用公共动作口径。

## 仍需目标项目验证

- 每个接入单据的可见性校验是否完整。
- 每个业务动作是否都在状态变化前后写入日志。
- 差异明细是否覆盖目标项目新增字段。
