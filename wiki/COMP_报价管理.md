# COMP 报价管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 销售 |
| 复用等级 | 可参考改造 |
| 适用项目 | 外贸、制造、定制产品报价 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation`、`RAW/PROJECTs/qmy-admin/src/api/sed/sales/quotation.ts`、`RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`、`RAW/docs/zhongsheng` |

## 业务目标

支持业务员根据客户、产品、材料、工价、包材、物流、税费、管理费等生成报价单，并通过历史报价参考和成本核算减少重复劳动。

## 前端入口

- 页面：`RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation/index.vue`。
- API：`RAW/PROJECTs/qmy-admin/src/api/sed/sales/quotation.ts`。
- PRD 路由证据：`PRD_Detailed_V2.md` 中报价管理章节。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 报价分页 | GET | `/api/quote/page` | `QuoteController` |
| 报价详情 | GET | `/api/quote/{id}` | `QuoteController` |
| 新增报价 | POST | `/api/quote` | `QuoteController` |
| 编辑报价 | PUT | `/api/quote` | `QuoteController` |
| 删除报价 | DELETE | `/api/quote/{id}` | `QuoteController` |
| 报价列表 | POST | `/sal/sed/quotation/list` | `qmy-admin` API |
| 成本详情 | POST | `/sal/sed/quotation/getCostDetail` | `qmy-admin` API |
| 历史报价 | POST/GET | `/sal/sed/quotation/getHistoryQuotation*` | `qmy-admin` API |
| 报价转订单 | POST | `/sal/sed/quotation/oneKeyToOrder`、`/skuToOrder` | `qmy-admin` API |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `quote` | `code`、`customer_id`、`quote_date`、`valid_date`、`total_amount`、`status` | 报价主表 |
| `quote_item` | `quote_id`、`product_id`、`product_name`、`quantity`、`unit_price`、`amount` | 报价明细 |
| 成本明细 | 材料、工价、包材、物流、税费、管理费 | 业务需求明确，代码需按项目补齐 |

## 权限边界

- 报价应区分创建、编辑、提交审核、采购成本确认、物流成本确认、审核、转订单。
- 调研资料强调客户信息保护，报价参考时需控制客户敏感信息暴露。

## 接入步骤

1. 确认报价业务是简单 CRUD 还是包含审核、会签、成本确认和转订单。
2. 确认产品、材料、工价、包材、物流、税费、币种来源。
3. 设计 `quote` 与 `quote_item` 是否足够，复杂项目需补成本明细表。
4. 接入历史报价检索维度：客户、尺寸、工艺、产品配置。
5. 接入转订单规则。

## 验收清单

- [ ] 能按客户创建报价单。
- [ ] 能维护至少一条报价明细。
- [ ] 总价能正确汇总。
- [ ] 历史报价可按关键维度检索或明确暂不支持。
- [ ] 报价转订单能保留来源报价单。

## 已知风险

- `zhongsheng-AI` 的 `QuoteController` 是基础 CRUD；`qmy-admin` 前端 API 更复杂，但对应后端不在当前同一来源中。
- 成本核算规则高度行业相关，不应直接泛化成所有项目默认公式。

