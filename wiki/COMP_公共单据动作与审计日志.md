# COMP 公共单据动作与审计日志

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 公共单据支撑 |
| 快照路径 | `COMPONENTS/document-action` |
| 复用等级 | `reference`，可参考改造 |
| 适用项目 | 报价、订单、采购、生产等正式业务单据的动作日志、锁定、解锁、重新确认和负责人改派 |
| 主要来源 | `RAW/PROJECTs/qmy-zhongsheng-ai` |
| 依赖口径 | 必选 `auth-permission`；具体业务接入依赖对应单据组件 |

## 业务目标

把业务单据的正式动作统一沉淀为后端日志和可见审计记录，支撑确认、取消、删除、转单、解锁、重新确认、负责人改派和管理员全局操作日志查询。

本组件不是完整工作流引擎，不做多级审批，也不替代业务单据自身状态机。

## 组件快照

- 入口：`COMPONENTS/document-action/README.md`
- 来源：`COMPONENTS/document-action/SOURCE_MAP.md`
- API 契约：`COMPONENTS/document-action/docs/contracts/API_CONTRACT.md`
- 数据契约：`COMPONENTS/document-action/docs/contracts/DATA_CONTRACT.md`
- 权限契约：`COMPONENTS/document-action/docs/contracts/PERMISSION_CONTRACT.md`
- 验收证据：`COMPONENTS/document-action/docs/acceptance/REVIEW_EVIDENCE.md`

## 来源分层

| 层级 | 来源 | 结论 |
| :--- | :--- | :--- |
| GUIDE | `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_公共单据动作.md` | 明确状态、锁定、接口、日志和边界 |
| 后端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/document` | 覆盖动作日志、解锁、重新确认、负责人改派 |
| 操作日志 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/system` | 管理员全局审计入口 |
| 前端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/components/document-action-log-drawer` | 气泡时间线日志抽屉 |
| 数据 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-document-action.sql` | 覆盖动作日志与解锁请求表 |

## 能力清单

| 能力 | 后端证据 | 前端证据 | 说明 |
| :--- | :--- | :--- | :--- |
| 单据动作日志 | 有 | 有 | 支持状态、锁定状态和字段差异 |
| 管理员解锁 | 有 | 有 | 要求填写原因 |
| 重新确认 | 有 | 有 | 解锁修改后恢复锁定 |
| 负责人改派 | 有 | 有 | 管理员能力 |
| 系统操作日志 | 有 | 有 | 复用单据动作日志做全局审计 |
| 旧申请/审批兼容 | 有 | 前端隐藏 | 接口保留，当前交互收口为管理员解锁 |

## 数据结构

| 表 | 说明 |
| :--- | :--- |
| `document_action_log` | 单据动作日志 |
| `document_unlock_request` | 解锁申请和处理记录 |

业务单据自身还需要维护 `lock_state`、`needs_reconfirm`、`reconfirm_scope_json` 等字段。

## 权限边界

- 日志查看：`document:action-log:list`。
- 申请解锁和重新确认：`document:unlock:request`。
- 管理解锁和兼容审批：`document:unlock:approve`。
- 改派负责人：`document:owner:reassign`。
- 系统操作日志：`system:operation-log:page`。

普通用户必须按业务单据可见性校验；超级管理员 `*` 可访问全部。

## 接入步骤

1. 接入基座与权限。
2. 创建 `document_action_log` 和 `document_unlock_request`。
3. 为业务单据补锁定字段和 `documentType`。
4. 在确认、取消、删除、转单、解锁后修改和重新确认处写日志。
5. 前端接入日志抽屉和锁定/解锁/重新确认入口。
6. 按业务单据实现可见性校验。

## 验收清单

- [x] 后端、前端、SQL、GUIDE 和系统操作日志证据完整。
- [x] 已在报价、订单、采购、生产中形成复用口径。
- [x] 已明确不是完整工作流引擎。
- [ ] 目标项目接入时仍需逐个业务单据验证日志写入点和可见性。

## 已知风险

- 若业务单据没有正确实现可见性，日志接口会泄露单据动作。
- 若只接前端抽屉、不在后端状态动作中写日志，会形成假审计。
- 解锁后修改字段差异需要随目标项目字段扩展同步维护。
