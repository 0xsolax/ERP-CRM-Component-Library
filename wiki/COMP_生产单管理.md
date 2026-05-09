# COMP 生产单管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 生产履约 |
| 快照路径 | `COMPONENTS/production-management` |
| 复用等级 | `reference`，可参考改造 |
| 适用项目 | 订单确认后的生产总单、生产组、产品行进度、分批安排、产品行交货和生产单导出 |
| 主要来源 | `RAW/PROJECTs/qmy-zhongsheng-ai` |
| 依赖口径 | 必选 `auth-permission`、`product-material`、`order-management`、`document-action`；采购联动依赖 `purchase-supplier` |

## 业务目标

将确认订单后的生产履约收口到唯一生产总单，围绕产品行进度推进采购入库释放、分批安排、产品行交货和客户样板生产单导出。

## 组件快照

- 入口：`COMPONENTS/production-management/README.md`
- 来源：`COMPONENTS/production-management/SOURCE_MAP.md`
- API 契约：`COMPONENTS/production-management/docs/contracts/API_CONTRACT.md`
- 数据契约：`COMPONENTS/production-management/docs/contracts/DATA_CONTRACT.md`
- 权限契约：`COMPONENTS/production-management/docs/contracts/PERMISSION_CONTRACT.md`
- 验收证据：`COMPONENTS/production-management/docs/acceptance/REVIEW_EVIDENCE.md`

## 来源分层

| 层级 | 来源 | 结论 |
| :--- | :--- | :--- |
| GUIDE | `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_生产单管理.md` | 已覆盖业务规则、接口、表结构、流程和边界 |
| 后端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/production` | 覆盖生产组、生产总单、进度、分批安排、交货和导出 |
| 前端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/production` | 覆盖生产组、生产总单列表、详情和进度表 |
| 数据 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-production.sql` | 覆盖生产组、生产总单、进度和批次表 |
| 验收 | `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL62生产组生产总单与交货进度.md` 等 | 已有编译、导出模板和细节修正报告 |

## 能力清单

| 能力 | 后端证据 | 前端证据 | 说明 |
| :--- | :--- | :--- | :--- |
| 生产组主档 | 有 | 有 | 支持分页、下拉、保存和删除 |
| 来源订单唯一生产总单 | 有 | 有 | 订单确认后创建或复用 |
| 手工生产单 | 有 | 有 | 不绑定订单，仍可安排、交货和导出 |
| 产品行进度 | 有 | 有 | 跟踪订单、采购、入库、安排、交货数量 |
| 分批安排 | 有 | 有 | 校验可安排数量和生产组 |
| 产品行交货 | 有 | 有 | 录入本次交货，后端累加累计值 |
| 生产单导出 | 有 | 有 | 使用客户 `.xls` 模板，按产品行拆工作表 |
| 单据日志/解锁 | 依赖公共组件 | 有 | 通过 `document-action` 接入 |

## 数据结构

| 表 | 说明 |
| :--- | :--- |
| `production_group` | 生产组主档 |
| `production_order` | 生产总单 |
| `production_order_progress` | 产品行生产进度 |
| `production_order_batch` | 分批安排 |

关键字段包括 `order_type`、`master_order_key`、`line_key`、`order_qty`、`purchased_qty`、`inbound_qty`、`planned_qty`、`delivered_qty`、`progress_status`、`lock_state`。

## 权限边界

- 生产组使用 `production:group:*` 权限。
- 生产总单列表、详情、保存、安排、交货和导出使用 `production:order:*` 权限。
- 日志、解锁、重新确认和负责人改派使用 `document:*` 权限。
- 普通用户按生产总单负责人可见，超级管理员 `*` 可见全部。

## 接入步骤

1. 接入基座、认证权限、产品物料、订单管理和公共单据动作。
2. 创建生产相关四张表。
3. 接入生产组、生产总单列表、详情、进度表和导出模板。
4. 将订单确认动作接入生产总单创建或刷新。
5. 如接入采购，确保采购生成和入库只刷新既有生产总单。
6. 覆盖生产安排、交货、导出、权限和日志验收。

## 验收清单

- [x] 生产组、生产总单、进度、批次表有 SQL 证据。
- [x] 生产总单前后端入口完整。
- [x] 生产导出模板和后端导出实现已抽取。
- [x] 公共单据动作边界已标记为依赖。
- [ ] 目标项目接入后仍需真实数据库端到端验证。

## 已知风险

- 该组件不是完整 MES。
- 手工生产单当前不派生采购单。
- 库存扣减、工序报工、质检和客户签收不在本组件范围。
- 生产总单和采购、订单、公共单据动作耦合较强，不能只复制生产包。
