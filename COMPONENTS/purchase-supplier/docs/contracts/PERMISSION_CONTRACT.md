# PERMISSION_CONTRACT｜purchase-supplier

## 权限目标

采购供应商组件包含供应商、采购价格、客户、订单来源、库存、退货、付款和导出信息。权限必须覆盖：

- 菜单可见性。
- 按钮/操作权限。
- 后端接口权限。
- 采购数据范围。
- 供应商价格、付款和导出泄漏控制。

## 菜单权限

| 菜单 | 权限码 | 前端路由 | 后端校验 |
| :--- | :--- | :--- | :--- |
| 库存预警 | `pur:yt:storeWarning:list` | `/purchase/stock-warning` | `/pur/yt/storeWarning/list` |
| 待采购列表 | `pur:yt:applyPurchase:list` | `/purchase/pending` | `/pur/yt/applyPurchase/list` |
| 已采购列表 | `pur:yt:purchase:list` | `/purchase/purchased` | `/pur/yt/purchase/list` |
| 供应商列表 | `pur:yt:purchaseSupplier:list` | `/purchase/supplier` | `/pur/yt/supplier/list` |

## 操作权限

| 操作 | 权限码 | API | 来源状态 |
| :--- | :--- | :--- | :--- |
| 新增供应商 | `pur:yt:purchaseSupplier:add` | `POST /pur/yt/supplier/add` | 已声明 |
| 编辑供应商 | `pur:yt:purchaseSupplier:update` | `POST /pur/yt/supplier/update` | 已声明 |
| 供应商详情 | `pur:yt:purchaseSupplier:detail` | `GET /pur/yt/supplier/detail` | 已声明 |
| 供应商跟进/标签/联系人 | 待补 | `/follow/*`、`/label/*`、`/contact/*` | 多数注释状态 |
| 供应商规格对照 | 待补 | `/supplierSpecification*` | 注释状态 |
| 采购趋势 | `pur:yt:purchaseSupplier:getPurchaseTrends` | `POST /pur/yt/supplier/getPurchaseTrends` | 已声明 |
| 采购占比 | 待补 | `POST /pur/yt/supplier/getPurchaseRatio` | 注释状态 |
| 生成采购单 | `pur:yt:applyPurchase:saveOrUpdate` | `POST /pur/yt/applyPurchase/saveOrUpdate` | 已声明 |
| 更换供应商 | `pur:yt:applyPurchase:replaceSupplier` | `POST /pur/yt/applyPurchase/replaceSupplier` | 已声明 |
| 追加采购 | `pur:yt:applyPurchase:addPurchase` | `POST /pur/yt/applyPurchase/addPurchase` | 已声明 |
| 撤回申购 | 待补 | `POST /pur/yt/applyPurchase/withdraw` | 未声明 |
| 新增/编辑采购单 | `pur:yt:purchase:createOrUpdate` | `POST /pur/yt/purchase/createOrUpdate` | 已声明 |
| 采购单详情 | `pur:yt:purchase:detail` | `GET /pur/yt/purchase/detail` | 已声明 |
| 采购单产品/半成品 | 待补 | `/productList`、`/semiFinishedProductList` | 注释状态 |
| 采购退货 | `pur:yt:purchase:return` | `POST /pur/yt/purchase/return` | 已声明 |
| 退货记录/统计/详情 | 待补 | `/returnRecord`、`/returnStats`、`/returnDetail` | 注释状态 |
| 采购跟进 | `pur:yt:purchase:follow:createOrUpdate` | `POST /pur/yt/purchase/follow/createOrUpdate` | 写入已声明，列表注释 |
| 通知供应商 | 待补 | `POST /pur/yt/purchase/notify` | 注释状态 |
| 采购导出 | `pur:yt:purchase:export` | `GET /pur/yt/purchase/export` | 已声明，需范围校验 |
| 删除暂存采购单 | 待补 | `POST /pur/yt/purchase/delete` | 未声明 |
| 库存预警申购 | `pur:yt:storeWarning:submitApplyPurchase` | `POST /pur/yt/storeWarning/submitApplyPurchase` | 已声明 |
| 生成预警测试 | 待补 | `GET /pur/yt/storeWarning/generateWarning` | 未声明，应限制内部 |

## 数据范围

| 角色/岗位 | 可见范围 | 可操作范围 | 说明 |
| :--- | :--- | :--- | :--- |
| 采购员 | 本人负责或分配给本人的待采购/采购单 | 本人范围内授权动作 | 需按采购负责人或组织范围过滤 |
| 部门负责人 | 本部门采购数据 | 本部门授权动作 | 需接组织树 |
| 销售/跟单 | 与本人订单相关的采购状态片段 | 通常只读 | 不应看到全部供应商价格和付款 |
| 仓储 | 与入库/库存相关的采购明细 | 入库相关动作 | 完整采购价格/付款需限制 |
| 财务 | 付款、对账、应付相关采购单 | 财务动作 | 付款权限归财务组件 |
| 老板/超级管理员 | 租户内全量采购 | 审计允许的全量操作 | 仍需操作日志 |

## 后端守卫要求

- 供应商子资源必须反查供应商归属，再做租户和数据范围校验。
- 采购申请必须校验来源订单/库存预警和可采购数量。
- 采购单详情、导出、退货、通知供应商必须校验采购单范围。
- 删除/撤回/退货必须校验当前状态，避免重复处理。
- 采购导出不能绕过列表权限。

## 泄漏检查

- 前端是否隐藏无权限入口：路由有权限码，按钮权限仍需接入基座验证。
- 后端是否强制校验权限：来源仅部分声明；接入前必须补齐缺口。
- API 是否过滤内部字段：供应商价格、付款方式、付款金额、客户、订单来源应按角色过滤。
- 是否存在跨供应商/跨部门读取风险：存在，集中在详情、导出、退货、趋势统计和供应商规格对照。
