# API_CONTRACT｜order-management

## legacy CRUD API

来源：`backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/OrdersController.java`

| 能力 | 方法 | 路径 | 入参 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| 订单分页 | GET | `/api/orders/page` | `current`、`size`、`keyword` | 按编号模糊查询，按创建时间倒序 |
| 订单详情 | GET | `/api/orders/{id}` | `id` | 按主键返回 `Orders` |
| 新增订单 | POST | `/api/orders` | `Orders` | 保存订单主档 |
| 编辑订单 | PUT | `/api/orders` | `Orders` | 更新订单主档 |
| 删除订单 | DELETE | `/api/orders/{id}` | `id` | 按主键删除 |

legacy API 没有订单明细 controller、审核、采购申请、退货、仓储、财务能力；只适合基础 CRUD 参考。

## YT 复杂订单 API

来源：`backend/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtOrderController.java` 与 `frontend/qmy-admin/src/api/admin/sales/order.ts`

| 能力 | 方法 | 路径 | 权限码 | 数据权限/风险 |
| :--- | :--- | :--- | :--- | :--- |
| 新增/编辑订单 | POST | `/sal/yt/order/saveOrUpdate` | `sal:yt:order:save` | 需补订单客户范围、子表归属和状态校验 |
| 审核订单 | POST | `/sal/yt/order/audit` | `sal:yt:order:audit` | 需校验审核角色、状态和订单范围 |
| 订单列表 | POST | `/sal/yt/order/list` | `sal:yt:order:list` | 来源已标注 `@RequiresDataPermissions` |
| 列表详情 | GET | `/sal/yt/order/listDetail` | `sal:yt:order:detail` | 需校验订单范围 |
| 订单详情 | GET | `/sal/yt/order/detail` | 注释状态 | 权限注解在来源中被注释，接入前必须补齐 |
| 子订单详情 | GET | `/sal/yt/order/subDetail` | 注释状态 | 需按子订单反查订单范围 |
| 订单详情按钮 | GET | `/sal/yt/order/orderDetail` | 注释状态 | 需按订单范围控制 |
| 子订单详情列表 | POST | `/sal/yt/order/subDetailList` | 注释状态 | 需按订单范围控制 |
| 产品详情 | GET | `/sal/yt/order/productDetail` | 注释状态 | 产品敏感字段需按产品组件权限控制 |
| 订单详情产品 Tab | POST | `/sal/yt/order/orderDetailProductTab` | 注释状态 | 需按订单范围控制 |
| 设置汇率 | POST | `/sal/yt/order/setRate` | `sal:yt:order:setRate` | 汇率影响金额和利润，需限制角色 |
| 删除订单 | GET | `/sal/yt/order/delete` | `sal:yt:order:delete` | 来源为破坏性 GET；新项目应改 DELETE/POST 并补范围守卫 |
| 导出订单 | POST | `/sal/yt/order/export` | `sal:yt:order:export` | 导出客户、价格、成本字段，需强制数据范围 |
| 商品项退货 | POST | `/sal/yt/order/returnItem` | `sal:yt:order:returnItem` | 需校验数量、状态、订单范围 |
| 订单退货 | POST | `/sal/yt/order/orderReturnItem` | 注释状态 | 接入前必须补权限和订单范围 |
| 退货记录列表 | POST | `/sal/yt/order/returnOrderList` | 注释状态 | 接入前必须补权限和订单范围 |
| 半成品子订单详情 | POST | `/sal/yt/order/inCompleteList` | 注释状态 | 接入前必须补权限和订单范围 |
| 订单半成品详情 | POST | `/sal/yt/order/orderInCompleteList` | 注释状态 | 接入前必须补权限和订单范围 |
| 确认半成品 | POST | `/sal/yt/order/confirmInComplete` | 注释状态 | 状态变更，接入前必须补权限、范围、状态校验 |
| 操作记录 | GET | `/sal/yt/order/itemOperation` | 注释状态 | 操作记录含采购/库存/退货信息，需补范围控制 |
| 退货统计 | POST | `/sal/yt/order/return/stats` | 注释状态 | 后端存在；当前前端封装使用 `statsData` |
| 退货统计数据 | POST | `/sal/yt/order/return/statsData` | 注释状态 | 接入前必须补权限和范围 |
| 指定规格退货记录 | GET | `/sal/yt/order/return/returnListBySpec` | 注释状态 | 接入前必须补权限和范围 |
| 确认发货 | POST | `/sal/yt/order/confirmDelivery` | `sal:yt:order:confirmDelivery` | 需校验仓储结果、数量和订单范围 |
| 确认完成 | GET | `/sal/yt/order/confirmComplete` | `sal:yt:order:confirmComplete` | 来源为状态变更 GET；新项目应改 POST |
| 关闭订单 | POST | `/sal/yt/order/close` | 未声明 | 接入前必须补权限、范围、关闭前置校验 |
| 关闭预览 | GET | `/sal/yt/order/closePreview` | 未声明 | 涉及金额和数量，需补权限与范围 |
| 物流信息 | POST | `/sal/yt/order/deliveryInfo` | 注释状态 | 仓储/物流边界，需补权限和范围 |
| 包裹详情 | GET | `/sal/yt/order/packageDetail` | 注释状态 | 仓储/物流边界，需补权限和范围 |
| 修改发货方式 | POST | `/sal/yt/order/updateShippingMethod` | `sal:yt:order:updateShippingMethod` | 需校验状态和订单范围 |
| 物流导出 | POST | `/sal/yt/order/exportDelivery` | `sal:yt:order:exportDelivery` | 导出收货/物流字段，需强制数据范围 |
| 导入订单 | POST | `/sal/yt/order/importOrders` | 未声明 | 接入前必须补权限、模板校验、幂等和审计 |

## 采购申请边界 API

来源：`backend/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java`

| 能力 | 方法 | 路径 | 权限码 | 组件归属 |
| :--- | :--- | :--- | :--- | :--- |
| 保存采购申请 | POST | `/pur/yt/applyPurchase/saveOrUpdate` | `pur:yt:applyPurchase:saveOrUpdate` | 订单触发，采购组件承接 |
| 待采购列表 | POST | `/pur/yt/applyPurchase/list` | `pur:yt:applyPurchase:list` | `purchase-supplier` |
| 采购申请详情 | POST | `/pur/yt/applyPurchase/saveDetail` | `pur:yt:applyPurchase:saveDetail` | `purchase-supplier` |
| 更换供应商 | POST | `/pur/yt/applyPurchase/replaceSupplier` | `pur:yt:applyPurchase:replaceSupplier` | `purchase-supplier` |
| 可更换供应商 | POST | `/pur/yt/applyPurchase/listReplaceableSuppliers` | 注释状态 | `purchase-supplier` |
| 追加采购 | POST | `/pur/yt/applyPurchase/addPurchase` | `pur:yt:applyPurchase:addPurchase` | `purchase-supplier` |
| 撤回采购申请 | POST | `/pur/yt/applyPurchase/withdraw` | 未声明 | `purchase-supplier`，接入前补权限 |

## 前端辅助 API

| 能力 | 方法 | 路径 | 来源 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| 字典查询 | GET | `/sysDictionary/getByCode` | `order.ts` | 订单页面读取字典，归基座/基础数据 |
| 客户选择 | 多个 | `/sal/yt/customer/*` | `customer.ts` | 订单客户和地址依赖，归客户组件 |
| 产品选择 | 多个 | 产品 API | `product/index.ts` | 订单商品规格依赖，归产品物料组件 |
| 组织用户 | 多个 | 组织 API | `org.ts` | 业务员/跟单员依赖，归认证组织基座 |

## 请求与响应原则

- 列表接口必须支持分页、订单编号、客户、业务员、跟单员、状态、交期、来源平台和数据范围过滤。
- 详情接口必须返回订单主档、子订单、商品项、操作记录、退货、发货、关闭、采购申请边界信息。
- 写接口必须先校验订单存在、租户一致、订单范围允许、状态允许，再修改主表或子表。
- 导出接口必须复用列表数据范围，不允许通过导出绕过页面权限。
- 采购申请必须校验订单商品项仍可采购，且避免重复申请。
