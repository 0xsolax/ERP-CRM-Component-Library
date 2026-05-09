# GUIDE_公共单据动作

| 字段 | 内容 |
| :--- | :--- |
| 文档类型 | GUIDE |
| 版本号 | v1.4.0 |
| 创建日期 | 2026-05-01 |
| 负责人 | Solazhu |
| 适用范围 | SOL-59 / MIG-06 公共单据状态、动作日志、锁定、解锁与负责人改派 |

---

## 1. 功能定位

公共单据动作层负责把报价、订单、后续采购单、生产单中的正式状态动作统一收口到后端接口，并沉淀动作日志、字段差异、锁定状态、解锁、重新确认和负责人改派记录；系统管理的“操作日志”第一阶段复用同一张 `document_action_log`，提供管理员全局审计入口。 - v1.4.0

当前已接入的真实单据：

- 报价：`documentType=quote`。 - v1.0.0
- 订单：`documentType=order`。 - v1.0.0
- 采购：`documentType=purchase`，覆盖独立采购、订单生成采购、确认、取消、备注更新、数量入库、全部入库和自动完单日志。 - v1.1.0
- 生产：`documentType=production`，当前接入日志查询、解锁、重新确认和负责人改派；生产安排、交货等业务动作日志后续随生产动作细化继续补齐。 - v1.3.0

## 2. 旧仓 GUIDE 对照

已对照旧仓：

```text
/Users/solazhu/software/zhongsheng-ai/docs/guide/GUIDE_报价管理.md
/Users/solazhu/software/zhongsheng-ai/docs/guide/GUIDE_订单管理.md
/Users/solazhu/software/zhongsheng-ai/docs/guide/GUIDE_采购单管理.md
/Users/solazhu/software/zhongsheng-ai/docs/guide/GUIDE_生产单管理.md
```

采纳的旧口径：

- 报价确认后写入确认态历史，转订单后报价侧核心字段锁定。 - v1.0.0
- 订单确认是正式动作，PI/合同输出只读订单快照。 - v1.0.0
- 报价转订单时订单继承报价客户、币种、备注和产品快照。 - v1.0.0
- 正式动作必须由后端接口落库，不把状态变化只做成前端隐藏按钮。 - v1.0.0
- 解锁后的修改必须重新确认，形成可追溯日志。 - v1.0.0

新基座取舍：

- 不照搬旧页面，列表与详情页只复原“日志抽屉、锁定提示、解锁/重新确认/改派”交互语义；申请解锁与审批入口本阶段暂隐藏。 - v1.3.0
- 字段、接口、权限串按新基座 `document:*` 体系重建。 - v1.0.0
- 不迁移旧日志、旧解锁申请、旧订单/报价数据。 - v1.0.0

## 3. 状态与锁定

公共锁定状态：

| 状态 | 含义 |
| :--- | :--- |
| `open` | 未锁定，通常对应草稿 |
| `locked` | 已锁定，通常对应确认、转单或取消后的正式态 |
| `pending_unlock` | 旧申请解锁流程的兼容状态；本阶段前端不再新建 |
| `temporary_unlocked` | 管理员已解锁，可临时修改，保存后必须重新确认 |

公共重新确认字段：

| 字段 | 说明 |
| :--- | :--- |
| `needs_reconfirm` | 是否需要重新确认 |
| `reconfirm_scope_json` | 重新确认范围，例如核心字段、产品快照、PI 信息 |

报价当前状态：

- `draft`：草稿，可编辑、删除、确认。 - v1.0.0
- `confirmed`：已确认，`lock_state=locked`，可导出、转订单。 - v1.0.0
- `converted`：已转订单，`lock_state=locked`，不可重复转单。 - v1.0.0

订单当前状态：

- `draft`：草稿，可编辑、删除、确认。 - v1.0.0
- `confirmed`：已确认，`lock_state=locked`。 - v1.0.0
- `cancelled`：已取消，`lock_state=locked`。 - v1.0.0

## 4. 后端接口

| 接口 | 权限 | 说明 |
| :--- | :--- | :--- |
| `POST /document/actionLog/page` | `document:action-log:list` | 查询当前用户可见单据的动作日志 |
| `POST /document/unlock/request` | `document:unlock:request` | 兼容旧申请解锁流程；本阶段前端隐藏入口 |
| `POST /document/unlock/warning` | `document:unlock:approve` | 管理员解锁，弹出提醒并要求填写原因，直接进入已解锁；兼容旧 `pending_unlock` 单据 |
| `POST /document/unlock/approve` | `document:unlock:approve` | 兼容旧申请审批；本阶段前端隐藏入口 |
| `POST /document/unlock/reject` | `document:unlock:approve` | 兼容旧申请审批；本阶段前端隐藏入口 |
| `POST /document/reconfirm` | `document:unlock:request` | 解锁修改后重新确认并锁定 |
| `POST /document/owner/assign` | `document:owner:reassign` | 管理员改派负责人，并同步关联报价/订单 |
| `POST /system/operationLog/page` | `system:operation-log:page` | 管理员全局分页查询单据动作审计日志 |

接口可见性：

- 超级管理员拥有 `*` 时可访问全部公共单据动作。 - v1.0.0
- 管理员审批/改派接口按 `document:unlock:approve`、`document:owner:reassign` 放行。 - v1.0.0
- 普通用户查询日志、重新确认时，后端仍按单据 `owner_id` 校验可见性。 - v1.3.0

## 5. 动作日志

数据表：

- `document_action_log`：记录动作类型、操作者、动作时间、前后状态、前后锁定状态、原因、差异摘要和差异明细。 - v1.0.0
- `document_unlock_request`：记录解锁、旧解锁申请、审批人、处理状态与处理时间。 - v1.3.0

当前动作类型：

| 动作类型 | 说明 |
| :--- | :--- |
| `confirm` | 确认报价或订单 |
| `cancel` | 取消订单 |
| `delete` | 删除草稿单据，必须在逻辑删除前写入 |
| `convert_order` | 报价转订单 |
| `create_from_quote` | 由报价创建订单 |
| `request_unlock` | 申请解锁 |
| `warning_unlock` | 解锁 |
| `approve_unlock` | 同意解锁 |
| `reject_unlock` | 拒绝解锁 |
| `update_after_unlock` | 解锁后保存修改 |
| `reconfirm` | 重新确认并锁定 |
| `assign_owner` | 管理员改派负责人 |
| `sync_assign_owner` | 同步改派关联单据负责人 |

## 6. 前端交互

通用组件：

- `zhongsheng-admin/src/components/document-action-log-drawer/index.vue`。 - v1.0.0

当前接入位置：

- 报价列表与报价表单：查看日志、锁定提示、解锁、重新确认、负责人改派。 - v1.3.0
- 订单列表与订单表单：查看日志、锁定提示、解锁、重新确认、负责人改派。 - v1.3.0
- 采购详情：查看日志、锁定提示、解锁、重新确认、负责人改派。 - v1.3.0
- 生产总单列表与详情：查看日志、锁定提示、解锁、重新确认、负责人改派。 - v1.3.0
- 系统管理 / 操作日志：管理员按时间、账号、模块、单据类型、动作和关键字查询全局单据审计日志。 - v1.4.0

交互规则：

- 日志按钮按 `document:action-log:list` 显示。 - v1.0.0
- 日志抽屉必须使用气泡时间线呈现，单条气泡内完整展示动作、摘要、操作者、时间、状态变化、锁定变化、原因和字段明细；字段明细不做表格单元格截断。 - v1.2.0
- 已锁定单据显示锁定提示；申请解锁入口暂隐藏。 - v1.3.0
- 管理员可在日志抽屉中对 `locked` 或旧 `pending_unlock` 单据执行“解锁”，弹出解锁提醒并要求填写原因。 - v1.3.0
- 解锁后表单允许保存，保存后展示待重新确认状态。 - v1.3.0
- 重新确认后单据恢复 `lock_state=locked`，`needs_reconfirm=false`。 - v1.0.0

## 7. 当前边界

1. 本轮不是完整工作流引擎，也不做多级审批。 - v1.0.0
2. 日志是审计记录，不允许作为普通备注编辑。 - v1.0.0
3. 当前已接入报价、订单、采购和生产总单；后续新增单据必须复用本公共接口和表结构，再补对应适配。 - v1.2.0
4. 重新确认当前只收口锁定与日志闭环，不额外创建报价历史新版本；报价历史仍由报价确认动作负责。 - v1.0.0
5. `complete` 动作已由采购自动完单接入；报价、订单仍无独立完结业务口径。 - v1.1.0
6. 操作日志第一阶段只覆盖 `document_action_log` 中的单据动作；员工账号、角色权限、产品/材料/客户/供应商等非单据操作审计留到后续统一审计表或显式业务日志扩展。 - v1.4.0
7. 真实数据库权限矩阵联调仍需在后续总体验收中覆盖。 - v1.0.0

## Change Logs

| 日期 | 版本号 | 变更描述 | 负责人 |
| :--- | :--- | :--- | :--- |
| 2026-05-06 | v1.4.0 | 新增系统管理操作日志口径，复用单据动作日志做全局审计，并要求删除草稿单据前写入 `delete` 日志 | Codex |
| 2026-05-05 | v1.3.0 | 收口单据解锁入口：前端隐藏申请解锁和审批按钮，仅保留管理员“解锁”入口，并明确旧接口仅作兼容 | Solazhu |
| 2026-05-04 | v1.2.0 | 按客户会议后修正要求，日志抽屉改为气泡时间线，并将生产总单接入公共解锁、日志、重新确认和改派入口 | Solazhu |
| 2026-05-01 | v1.1.0 | SOL-61 接入采购单动作日志，补充采购确认、入库、取消和自动完单口径 | Solazhu |
| 2026-05-01 | v1.0.0 | SOL-59 首次建立公共单据动作 GUIDE，记录状态、日志、锁定、解锁、重新确认和负责人改派口径 | Solazhu |
