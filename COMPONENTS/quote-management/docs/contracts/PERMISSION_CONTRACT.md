# PERMISSION_CONTRACT｜quote-management

## 权限码

| 能力 | 权限码 |
| :--- | :--- |
| 报价列表 | `sal:sed:quotation:quotation:list` |
| 报价详情 | `sal:sed:quotation:quotationDetail` |
| 新增/编辑/再次创建 | `sal:sed:quotation:saveOrUpdate` |
| 采购成本详情 | `sal:sed:quotation:procurementDetail` |
| 采购成本确认 | `sal:sed:quotation:procurementConfirm` |
| 物流成本详情 | `sal:sed:quotation:logisticsDetail` |
| 物流成本确认 | `sal:sed:quotation:logisticsConfirm` |
| 导出报价单 | `sal:sed:quotation:exportQuotation` |
| 提交审核 | `sal:sed:quotation:submitAudit` |
| 审核/会签 | `sal:sed:quotation:audit` |
| 历史报价列表 | `sal:sed:quotation:getHistoryQuotation` |
| 历史报价详情 | `sal:sed:quotation:getHistoryQuotationDetail` |
| 历史报价信息 | `sal:sed:quotation:getHistoryQuotationInfo` |
| 成本明细 | `sal:sed:quotation:getCostDetail` |
| 一键转订单 | `sal:sed:quotation:oneKeyToOrder` |
| 单 SKU 转订单 | `sal:sed:quotation:skuToOrder` |
| 合并转订单列表 | `sal:sed:quotation:mergeList` |
| 合并转订单 | `sal:sed:quotation:mergeToOrder` |
| 字典列表 | `sal:sed:quotation:getCurrencyList`、`getTaxList`、`getFobList`、`getExwList` |
| 总裁微信审核 | `sal:sed:quotation:presidentWxAudit` |

## 数据范围

报价数据范围至少应支持：

- 本人：只看本人创建或本人负责客户的报价。
- 部门：看本部门业务员或本部门客户报价。
- 全公司：看当前租户内全部报价。
- 老板视角：按项目规则看全部公司或指定组织报价。

## 敏感字段

| 字段/信息 | 风险 | 控制建议 |
| :--- | :--- | :--- |
| 客户名称、收货地址 | 客户资料泄露 | 跟随客户数据范围 |
| 报价、基础报价、订单金额 | 商业价格泄露 | 后端权限控制，不只靠前端隐藏 |
| 采购成本、物流成本、总成本 | 成本泄露 | 财务、采购、管理角色可见 |
| 毛利率、均价、中位数 | 商业策略泄露 | 老板、财务或授权销售可见 |
| 历史报价跨客户趋势 | 客户敏感信息泄露 | 脱敏或限制角色 |
| 微信审核凭证图片 | 文件泄露 | 依赖 file-oss 权限和签名策略 |

## 来源风险

- qmy-java 来源只有列表接口标注 `@RequiresDataPermissions`。
- 详情、成本确认、保存、审核、历史报价、成本明细和转订单接口需要在目标项目补充按报价 ID 的数据范围校验。
- 前端按钮权限只控制入口展示，不能作为安全边界。
- 导出接口必须复用同一套数据范围，否则可能批量导出越权报价。

## 推荐后端守卫

目标项目接入时建议统一实现：

1. `assertQuotationVisible(quotationId, currentUser)`：详情、历史、成本明细使用。
2. `assertQuotationWritable(quotationId, currentUser, action)`：保存、成本确认、提交审核、驳回、会签使用。
3. `assertQuotationTransferable(quotationId, currentUser)`：转订单使用，校验状态和重复转换。
4. `assertCustomerVisible(customerId, currentUser)`：新建报价选择客户、历史报价跨客户参考使用。
