# COMP 报价管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 销售 |
| 快照目录 | `COMPONENTS/quote-management` |
| 复用等级 | `reference` |
| 适用项目 | 外贸、制造、定制产品报价、复杂成本核算 |
| 来源路径 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation`、`RAW/docs/zhongsheng` |

## 业务目标

支持销售基于客户、产品 SKU、包材、配件、工艺、物流、税费、币种和汇率生成报价，并通过成本确认、历史报价参考、审核会签和转订单能力完成销售前置流程。

## 来源层次

| 层次 | 来源 | 结论 |
| :--- | :--- | :--- |
| 基础 CRUD | `zhongsheng-AI` 的 `QuoteController`、`Quote`、`init.sql`、`PRD_Detailed_V2.md` | 只覆盖报价主档基础增删改查 |
| 复杂报价流 | qmy-java `SalSedQuotationController/Service/Manager` 与 qmy-admin SED 报价页 | 覆盖成本、历史报价、审核、导出和转订单 |

## 前端入口

- 页面：`COMPONENTS/quote-management/frontend/qmy-admin/src/views/sed/sales/quotation`。
- API：`COMPONENTS/quote-management/frontend/qmy-admin/src/api/sed/sales/quotation.ts`。
- 路由：`/sales/quotation`，权限 `sal:sed:quotation:quotation:list`。
- 快照路由已裁剪为报价入口，客户和订单入口由对应组件提供。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 基础报价分页 | GET | `/api/quote/page` | `zhongsheng-AI QuoteController` |
| 基础报价详情 | GET | `/api/quote/{id}` | `zhongsheng-AI QuoteController` |
| 基础报价新增 | POST | `/api/quote` | `zhongsheng-AI QuoteController` |
| 基础报价编辑 | PUT | `/api/quote` | `zhongsheng-AI QuoteController` |
| 基础报价删除 | DELETE | `/api/quote/{id}` | `zhongsheng-AI QuoteController` |
| 复杂报价列表 | POST | `/sal/sed/quotation/list` | qmy-java/qmy-admin |
| 报价详情 | GET | `/sal/sed/quotation/quotationDetail` | qmy-java/qmy-admin |
| 保存/再次创建 | POST | `/sal/sed/quotation/saveOrUpdate` | qmy-java/qmy-admin |
| 业务员客户信息 | GET | `/sal/sed/quotation/getUserInfo` | qmy-java 后端 reference |
| 成本明细 | POST | `/sal/sed/quotation/getCostDetail` | qmy-java/qmy-admin |
| 采购/物流成本确认 | POST | `/procurementConfirm`、`/logisticsConfirm` | qmy-java/qmy-admin |
| 历史报价 | POST/GET | `/getHistoryQuotation*` | qmy-java/qmy-admin |
| 审核/会签 | POST | `/submitAudit`、`/audit`、`/jointAudit`、`/presidentWxAudit` | qmy-java/qmy-admin |
| 转订单 | POST | `/oneKeyToOrder`、`/skuToOrder`、`/mergeToOrder` | qmy-java/qmy-admin |

## 数据结构

| 模型 | 表/对象 | 说明 |
| :--- | :--- | :--- |
| 基础 CRUD | `quote`、`quote_item` | legacy 报价主表和明细表 |
| 复杂报价 | `sal_sed_quotation` | 报价主档、客户、业务员、成本、金额、状态、币种、税、汇率 |
| SKU 报价 | `sal_sed_quotation_sku` | 产品、搭配、SKU、基础报价、最终报价、数量、体积 |
| SKU 包材 | `sal_sed_quotation_sku_packing` | 包材、装箱数、包材数量、成本、尺寸 |
| 操作历史 | `sal_sed_quotation_history` | 提交审核、成本确认、驳回、会签等操作记录 |

## 权限边界

- 报价应区分列表、详情、保存、采购成本、物流成本、提交审核、会签审核、历史报价、导出、转订单、微信审核。
- 报价包含客户、地址、成本、毛利率和历史价格，前端按钮权限不能替代后端数据范围。
- qmy-java 来源只有列表接口标注 `@RequiresDataPermissions`；详情、成本、审核、历史和转订单在目标项目接入前必须补报价归属与客户范围校验。
- `/getUserInfo` 返回业务员客户信息，虽当前前端报价 API 未直接封装，也必须纳入客户敏感信息边界。
- 历史报价跨客户趋势需要脱敏或限制角色，避免泄露客户价格策略。

## 接入步骤

1. 确认使用基础 CRUD 还是复杂报价流。
2. 先完成产品物料、客户管理、基础数据、文件和权限组件。
3. 建立报价主表、报价 SKU、报价包材和报价历史表。
4. 接入报价列表、详情、保存、成本确认、审核、历史报价、导出和转订单 API。
5. 接入 qmy-admin SED 报价页面，并映射目标基座的用户 store、下载、上传和产品选择组件。
6. 补齐后端数据范围、导出范围和转订单幂等校验。

## 验收清单

- [x] 基础 CRUD 与复杂报价流差异已明确。
- [x] 成本明细、历史报价、审核、转订单均有来源标注。
- [x] 客户敏感信息、成本和历史报价参考边界已写入组件契约。
- [ ] 目标项目已完成真实数据库 DDL 和接口联调。
- [ ] 目标项目已完成数据权限和导出权限验证。

## 已知风险

- `zhongsheng-AI` `Quote` 实体存在 `products` 字段，但 legacy SQL 未定义该列，且 `quote_item` 未在 CRUD 中形成完整子表保存。
- qmy-java 未提供完整报价 DDL，只能按实体与 Mapper XML 反推。
- SED 成本公式是伞业场景口径，新行业必须重新确认公式。
