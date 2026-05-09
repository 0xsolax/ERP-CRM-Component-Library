# document-action 公共单据动作与审计日志

## 定位

本组件沉淀跨业务单据的公共动作层，用于统一报价、订单、采购、生产等单据的动作日志、锁定、解锁、重新确认、负责人改派和系统操作日志查询。

它不是完整工作流引擎，也不替代具体业务单据的状态机。

## 能力清单

- 单据动作日志分页。
- 解锁申请接口兼容。
- 管理员警告解锁。
- 重新确认并恢复锁定。
- 负责人改派。
- 字段差异摘要和明细。
- 前端气泡时间线日志抽屉。
- 系统管理操作日志页面。

## 来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_公共单据动作.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/document`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/components/document-action-log-drawer`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-document-action.sql`

## 接入步骤

1. 接入基座和认证权限。
2. 创建 `document_action_log` 和 `document_unlock_request`。
3. 为目标业务单据定义 `documentType`、状态和锁定字段。
4. 在确认、取消、删除、转单、解锁后修改、重新确认等动作中写日志。
5. 前端接入日志抽屉、锁定提示、管理员解锁和重新确认入口。
6. 按业务单据实现可见性校验。

## 边界

- 不做多级审批。
- 不做完整流程编排。
- 不保存普通备注。
- 不替代报价、订单、采购、生产自身状态前置校验。
