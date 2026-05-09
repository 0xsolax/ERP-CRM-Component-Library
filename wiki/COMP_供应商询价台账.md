# COMP 供应商询价台账

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 采购前询价 |
| 快照路径 | `COMPONENTS/supplier-inquiry` |
| 复用等级 | `reference`，可参考改造 |
| 适用项目 | 供应商价格历史、采购前询价、人工比价资料沉淀 |
| 主要来源 | `RAW/PROJECTs/qmy-zhongsheng-ai` |
| 依赖口径 | 必选 `purchase-supplier`、`product-material`、`auth-permission` |

## 业务目标

记录采购前向供应商询价的历史价格和条件，支持按供应商、询价对象、币种、日期和有效状态筛选，服务后续人工比价与采购判断。

本组件不自动生成采购单，不回写主档价格，也不进入库存、入库或付款链路。

## 组件快照

- 入口：`COMPONENTS/supplier-inquiry/README.md`
- 来源：`COMPONENTS/supplier-inquiry/SOURCE_MAP.md`
- API 契约：`COMPONENTS/supplier-inquiry/docs/contracts/API_CONTRACT.md`
- 数据契约：`COMPONENTS/supplier-inquiry/docs/contracts/DATA_CONTRACT.md`
- 权限契约：`COMPONENTS/supplier-inquiry/docs/contracts/PERMISSION_CONTRACT.md`
- 验收证据：`COMPONENTS/supplier-inquiry/docs/acceptance/REVIEW_EVIDENCE.md`

## 来源分层

| 层级 | 来源 | 结论 |
| :--- | :--- | :--- |
| GUIDE | `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md` | 明确采购前台账定位和不回写主档边界 |
| 后端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/supplier` | 覆盖询价 CRUD、历史查询、编辑日志和供应商快照 |
| 前端 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/supplier-inquiry` | 覆盖列表、筛选、新增、详情、历史抽屉和快捷新增 |
| 数据 | `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-supplier-inquiry.sql` | 覆盖 `supplier_inquiry` 表 |
| 验收 | `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0506_供应商询价台账开发.md` 等 | 已有开发、修复和展示口径报告 |

## 能力清单

| 能力 | 后端证据 | 前端证据 | 说明 |
| :--- | :--- | :--- | :--- |
| 询价分页筛选 | 有 | 有 | 关键词、供应商、对象类型、币种、日期、有效状态 |
| 新增/编辑/详情/删除 | 有 | 有 | 删除为逻辑删除 |
| 供应商快捷新增 | 依赖供应商主档 | 有 | 新增后刷新下拉并回填 |
| 询价对象选择 | 有 | 有 | 支持材料、面料、包材、伞架、产品和手工对象 |
| 供应商历史 | 有 | 有 | 可从台账或供应商列表查看 |
| 对象历史 | 有 | 有 | 主档对象按 ID，手工对象按类型和名称 |
| 编辑日志 | 有 | 有 | 后端比较字段差异，详情展示日志 |

## 数据结构

核心表：`supplier_inquiry`。

关键字段包括 `supplier_id`、`supplier_code`、`supplier_name`、`target_type`、`target_id`、`target_code`、`target_name`、`specification`、`unit`、`price`、`currency`、`tax_rate`、`moq`、`delivery_days`、`quote_date`、`valid_until`、`contact_name`、`owner_id`、`modification_log_json`。

## 权限边界

- 列表和历史：`supplier:inquiry:page`。
- 详情：`supplier:inquiry:detail`。
- 新增和编辑：`supplier:inquiry:save`。
- 删除：`supplier:inquiry:remove`。
- 供应商快捷新增依赖供应商保存权限。

## 接入步骤

1. 接入供应商主档、产品物料和权限基座。
2. 创建 `supplier_inquiry` 表。
3. 接入采购管理下的供应商询价菜单。
4. 接入询价页面、历史抽屉和快捷新增交互。
5. 覆盖编辑日志、逻辑删除和权限验收。

## 验收清单

- [x] 前端页面、API、后端接口、SQL 和 GUIDE 证据完整。
- [x] 已明确不回写主档价格、不自动生成采购单。
- [x] 支持供应商历史和对象历史。
- [ ] 正式比价单、采用报价到采购单、供应商评分仍需另行设计。

## 已知风险

- 询价台账容易被误解为采购审批或比价单，本组件只沉淀询价事实。
- 手工对象不会生成主档，后续统计需要按目标项目规则清洗。
- 若目标项目需要附件、图片识别或供应商评分，需要扩展数据结构。
