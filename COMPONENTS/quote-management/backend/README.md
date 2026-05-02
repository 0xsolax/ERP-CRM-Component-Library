# 后端快照说明

## 来源层次

| 层次 | 快照路径 | 定位 |
| :--- | :--- | :--- |
| legacy CRUD | `zhongsheng-AI/erp-backend/src/main/java/com/erp` | 对应 `PRD_Detailed_V2.md` 的基础报价 CRUD |
| SED 复杂流 | `qmy-java` | 对应 qmy-admin `/sal/sed/quotation/*` 的复杂报价、成本、审核、历史报价和转订单 |

## legacy CRUD

- `QuoteController` 提供 `/api/quote/page`、`/{id}`、`POST /api/quote`、`PUT /api/quote`、`DELETE /api/quote/{id}`。
- `QuoteServiceImpl` 仅提供按报价编号模糊搜索和 `createdAt` 倒序分页。
- `Quote` 实体保留 `products` 字符串字段，但 `init.sql` 中 `quote` 表没有该列。
- `quote_item` 表存在于 SQL，但 legacy CRUD 未形成完整明细保存链路。

## SED 复杂流

- `SalSedQuotationController` 提供报价列表、详情、采购成本、物流成本、保存/再次创建、审核、会签、历史报价、成本明细、导出、转订单等接口。
- `SalSedQuotationService` 承担状态流转、成本确认、审核状态、详情组装和入口校验。
- `SalSedQuotationManager` 承担成本明细、历史报价趋势、导出、转订单和总成本计算。
- `SalSedQuotationSkuManager`、`SalSedQuotationSkuPackingManager` 承担体积、箱数和 SKU/包材聚合。
- 订单相关 entity/mapper 只作为报价转订单引用证据，不代表完整订单组件。

## 迁移风险

- qmy-java 没有完整 DDL，接入时必须按 `db/qmy-java/sal-sed-quotation-schema-notes.md` 补齐表结构。
- qmy-java 复杂流依赖大量产品、客户、文件、字典和订单服务，不能单独编译。
- 来源 controller 多数接口只有 `@RequiresPermissions`，缺少报价归属和客户范围的统一守卫；目标项目必须补数据权限。
