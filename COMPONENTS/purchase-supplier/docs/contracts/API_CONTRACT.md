# API_CONTRACT｜purchase-supplier

## legacy 供应商 CRUD API

来源：`backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/SupplierController.java`

| 能力 | 方法 | 路径 | 入参 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| 供应商分页 | GET | `/api/supplier/page` | `current`、`size`、`keyword` | 按编号/名称模糊查询 |
| 供应商详情 | GET | `/api/supplier/{id}` | `id` | 按主键返回 `Supplier` |
| 新增供应商 | POST | `/api/supplier` | `Supplier` | 保存供应商 |
| 编辑供应商 | PUT | `/api/supplier` | `Supplier` | 更新供应商 |
| 删除供应商 | DELETE | `/api/supplier/{id}` | `id` | 按主键删除 |

legacy API 只适合供应商基础档案，不覆盖采购申请、采购单、库存预警、付款和退货。

## YT 供应商 API

来源：`PurYtSupplierController.java` 与 `frontend/qmy-admin/src/api/admin/purchase/supplier.ts`

| 能力 | 方法 | 路径 | 权限码 | 来源状态 |
| :--- | :--- | :--- | :--- | :--- |
| 供应商下拉 | GET | `/pur/yt/supplier/supplierSelect` | 注释状态 | 接入前补权限 |
| 新增供应商 | POST | `/pur/yt/supplier/add` | `pur:yt:purchaseSupplier:add` | 已声明 |
| 供应商列表 | POST | `/pur/yt/supplier/list` | `pur:yt:purchaseSupplier:list` | 已声明 |
| 编辑供应商 | POST | `/pur/yt/supplier/update` | `pur:yt:purchaseSupplier:update` | 已声明 |
| 供应商详情 | GET | `/pur/yt/supplier/detail` | `pur:yt:purchaseSupplier:detail` | 已声明 |
| 新增/编辑跟进 | POST | `/pur/yt/supplier/follow/createOrUpdate` | 注释状态 | 接入前补权限和范围 |
| 删除跟进 | GET | `/pur/yt/supplier/follow/delete` | 注释状态 | 来源为破坏性 GET，需收口 |
| 新增标签 | POST | `/pur/yt/supplier/label/add` | 注释状态 | 接入前补权限 |
| 批量新增标签 | POST | `/pur/yt/supplier/label/batchAdd` | 注释状态 | 接入前补权限 |
| 删除标签 | POST | `/pur/yt/supplier/label/delete` | 注释状态 | 接入前补权限 |
| 标签列表 | GET | `/pur/yt/supplier/label/list` | 注释状态 | 接入前补权限 |
| 保存联系人 | POST | `/pur/yt/supplier/contact/saveOrUpdate` | 注释状态 | 接入前补权限 |
| 删除联系人 | GET | `/pur/yt/supplier/contact/delete` | 注释状态 | 来源为破坏性 GET，需收口 |
| 联系人列表 | GET | `/pur/yt/supplier/contact/list` | 注释状态 | 接入前补权限 |
| 联系人详情 | GET | `/pur/yt/supplier/contact/detail` | 注释状态 | 接入前补权限 |
| 供应商规格对照 | POST | `/pur/yt/supplier/supplierSpecification` | 注释状态 | 接入前补权限 |
| 规格对照详情 | GET | `/pur/yt/supplier/supplierSpecification/detail` | 注释状态 | 接入前补权限 |
| 更新规格对照 | POST | `/pur/yt/supplier/supplierSpecification/update` | 注释状态 | 接入前补权限 |
| 采购趋势 | POST | `/pur/yt/supplier/getPurchaseTrends` | `pur:yt:purchaseSupplier:getPurchaseTrends` | 已声明 |
| 采购占比 | POST | `/pur/yt/supplier/getPurchaseRatio` | 注释状态 | 接入前补权限 |

## 待采购 API

来源：`PurYtApplyPurchaseController.java` 与 `frontend/qmy-admin/src/api/admin/purchase/pending.ts`

| 能力 | 方法 | 路径 | 权限码 | 来源状态 |
| :--- | :--- | :--- | :--- | :--- |
| 保存/生成采购申请 | POST | `/pur/yt/applyPurchase/saveOrUpdate` | `pur:yt:applyPurchase:saveOrUpdate` | 已声明 |
| 待采购列表 | POST | `/pur/yt/applyPurchase/list` | `pur:yt:applyPurchase:list` | 已声明 |
| 采购申请详情 | POST | `/pur/yt/applyPurchase/saveDetail` | `pur:yt:applyPurchase:saveDetail` | 已声明 |
| 更换供应商 | POST | `/pur/yt/applyPurchase/replaceSupplier` | `pur:yt:applyPurchase:replaceSupplier` | 已声明 |
| 可更换供应商 | POST | `/pur/yt/applyPurchase/listReplaceableSuppliers` | 注释状态 | 接入前补权限 |
| 追加采购 | POST | `/pur/yt/applyPurchase/addPurchase` | `pur:yt:applyPurchase:addPurchase` | 已声明 |
| 撤回申购 | POST | `/pur/yt/applyPurchase/withdraw` | 未声明 | 接入前补权限和状态校验 |
| 暂存采购单列表 | POST | `/pur/yt/purchase/listTemporary` | 注释状态 | 接入前补权限 |

## 已采购 API

来源：`PurYtPurchaseController.java` 与 `frontend/qmy-admin/src/api/admin/purchase/purchased.ts`

| 能力 | 方法 | 路径 | 权限码 | 来源状态 |
| :--- | :--- | :--- | :--- | :--- |
| 新增/编辑采购单 | POST | `/pur/yt/purchase/createOrUpdate` | `pur:yt:purchase:createOrUpdate` | 已声明 |
| 采购单详情 | GET | `/pur/yt/purchase/detail` | `pur:yt:purchase:detail` | 已声明 |
| 采购单列表 | POST | `/pur/yt/purchase/list` | `pur:yt:purchase:list` | 已声明 |
| 采购产品列表 | POST | `/pur/yt/purchase/productList` | 注释状态 | 接入前补权限 |
| 半成品列表 | POST | `/pur/yt/purchase/semiFinishedProductList` | 注释状态 | 接入前补权限 |
| 采购退货 | POST | `/pur/yt/purchase/return` | `pur:yt:purchase:return` | 已声明 |
| 退货记录 | POST | `/pur/yt/purchase/returnRecord` | 注释状态 | 接入前补权限 |
| 退货统计 | POST | `/pur/yt/purchase/returnStats` | 注释状态 | 接入前补权限 |
| 退货详情 | POST | `/pur/yt/purchase/returnDetail` | 注释状态 | 接入前补权限 |
| 跟进列表 | POST | `/pur/yt/purchase/follow/list` | 注释状态 | 接入前补权限 |
| 新增/编辑跟进 | POST | `/pur/yt/purchase/follow/createOrUpdate` | `pur:yt:purchase:follow:createOrUpdate` | 已声明 |
| 操作记录 | GET | `/pur/yt/purchase/itemOperation` | 注释状态 | 接入前补权限 |
| 通知供应商 | POST | `/pur/yt/purchase/notify` | 注释状态 | 接入前补权限 |
| 采购导出 | GET | `/pur/yt/purchase/export` | `pur:yt:purchase:export` | 已声明，需强制范围 |
| 删除暂存采购单 | POST | `/pur/yt/purchase/delete` | 未声明 | 接入前补权限 |

## 库存预警 API

来源：`PurYtStoreWarningController.java` 与 `frontend/qmy-admin/src/api/admin/purchase/stock-warning.ts`

| 能力 | 方法 | 路径 | 权限码 | 来源状态 |
| :--- | :--- | :--- | :--- | :--- |
| 库存预警列表 | POST | `/pur/yt/storeWarning/list` | `pur:yt:storeWarning:list` | 已声明 |
| 申购详情 | POST | `/pur/yt/storeWarning/applyDetail` | `pur:yt:storeWarning:applyDetail` | 已声明 |
| 提交申购 | POST | `/pur/yt/storeWarning/submitApplyPurchase` | `pur:yt:storeWarning:submitApplyPurchase` | 已声明 |
| 生成预警测试 | GET | `/pur/yt/storeWarning/generateWarning` | 未声明 | 接入前限制为内部工具或移除 |

## 请求与响应原则

- 列表接口必须支持分页、供应商、产品规格、客户、订单来源、采购状态和数据范围过滤。
- 采购单详情必须返回主档、明细、来源申请、产品/规格、退货、跟进、付款/入库边界信息。
- 写接口必须先校验供应商、产品规格、订单来源、租户、状态和数量，再修改主表或明细。
- 导出接口必须复用列表和详情的数据范围。
- 采购申请生成采购单必须具备重复生成防护。
