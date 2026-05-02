# API_CONTRACT｜quote-management

## legacy CRUD API

来源：`backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java`

| 能力 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 报价分页 | GET | `/api/quote/page` | 参数 `current`、`size`、`keyword` |
| 报价详情 | GET | `/api/quote/{id}` | 按主键获取报价 |
| 新增报价 | POST | `/api/quote` | 请求体为 `Quote` |
| 编辑报价 | PUT | `/api/quote` | 请求体为 `Quote` |
| 删除报价 | DELETE | `/api/quote/{id}` | 按主键删除 |

legacy API 只适合基础报价单，不覆盖成本、审核、历史报价和转订单。

## SED 复杂流 API

来源：`backend/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/sed/SalSedQuotationController.java` 与 `frontend/qmy-admin/src/api/sed/sales/quotation.ts`

| 能力 | 方法 | 路径 | 权限码 |
| :--- | :--- | :--- | :--- |
| 报价列表 | POST | `/sal/sed/quotation/list` | `sal:sed:quotation:quotation:list` |
| 报价详情 | GET | `/sal/sed/quotation/quotationDetail` | `sal:sed:quotation:quotationDetail` |
| 新增/编辑/再次创建 | POST | `/sal/sed/quotation/saveOrUpdate` | `sal:sed:quotation:saveOrUpdate` |
| 采购成本详情 | GET | `/sal/sed/quotation/procurementDetail` | `sal:sed:quotation:procurementDetail` |
| 采购成本确认 | POST | `/sal/sed/quotation/procurementConfirm` | `sal:sed:quotation:procurementConfirm` |
| 物流成本详情 | GET | `/sal/sed/quotation/logisticsDetail` | `sal:sed:quotation:logisticsDetail` |
| 物流成本确认 | POST | `/sal/sed/quotation/logisticsConfirm` | `sal:sed:quotation:logisticsConfirm` |
| 客户收货地址 | GET | `/sal/sed/quotation/getCustomerAddress` | `sal:sed:quotation:getCustomerAddress` |
| 导出报价单 | POST | `/sal/sed/quotation/exportQuotation` | `sal:sed:quotation:exportQuotation` |
| 提交审核 | POST | `/sal/sed/quotation/submitAudit` | `sal:sed:quotation:submitAudit` |
| 审核驳回 | POST | `/sal/sed/quotation/audit` | `sal:sed:quotation:audit` |
| 会签审核 | POST | `/sal/sed/quotation/jointAudit` | `sal:sed:quotation:audit` |
| 历史报价列表 | POST | `/sal/sed/quotation/getHistoryQuotation` | `sal:sed:quotation:getHistoryQuotation` |
| 历史报价详情 | GET | `/sal/sed/quotation/getHistoryQuotationDetail` | `sal:sed:quotation:getHistoryQuotationDetail` |
| 历史报价信息 | POST | `/sal/sed/quotation/getHistoryQuotationInfo` | `sal:sed:quotation:getHistoryQuotationInfo` |
| 成本明细 | POST | `/sal/sed/quotation/getCostDetail` | `sal:sed:quotation:getCostDetail` |
| 一键转订单 | POST | `/sal/sed/quotation/oneKeyToOrder` | `sal:sed:quotation:oneKeyToOrder` |
| 单 SKU 转订单 | POST | `/sal/sed/quotation/skuToOrder` | `sal:sed:quotation:skuToOrder` |
| 合并转订单列表 | POST | `/sal/sed/quotation/mergeList` | `sal:sed:quotation:mergeList` |
| 合并转订单 | POST | `/sal/sed/quotation/mergeToOrder` | `sal:sed:quotation:mergeToOrder` |
| 合并 SKU 列表 | POST | `/sal/sed/quotation/mergeSkuList` | `sal:sed:quotation:mergeList` |
| 币种列表 | GET | `/sal/sed/quotation/getCurrencyList` | `sal:sed:quotation:getCurrencyList` |
| 含税列表 | GET | `/sal/sed/quotation/getTaxList` | `sal:sed:quotation:getTaxList` |
| FOB 列表 | GET | `/sal/sed/quotation/getFobList` | `sal:sed:quotation:getFobList` |
| EXW 列表 | GET | `/sal/sed/quotation/getExwList` | `sal:sed:quotation:getExwList` |
| 币种字段显示 | GET | `/sal/sed/quotation/getDisplayFieldsByCurrency` | `sal:sed:quotation:getDisplayFieldsByCurrency` |
| 总裁微信审核 | POST | `/sal/sed/quotation/presidentWxAudit` | `sal:sed:quotation:presidentWxAudit` |

## 请求与响应原则

- 列表请求必须支持分页、报价编号、客户、状态、业务员和数据范围过滤。
- 详情响应必须返回主档、SKU、包材、成本、历史操作、审核状态和微信审核凭证。
- 写接口必须先校验报价存在、租户一致、数据范围允许、状态允许，再修改主表或子表。
- 历史报价接口必须限制客户敏感字段，跨客户趋势应按角色配置。
- 转订单接口必须具备幂等或重复转换防护。
