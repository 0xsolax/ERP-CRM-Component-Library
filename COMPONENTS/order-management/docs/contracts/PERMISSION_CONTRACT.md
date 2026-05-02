# PERMISSION_CONTRACT｜order-management

## 权限目标

订单组件包含客户、收货信息、价格、成本、供应商、库存、发货和回款状态。权限必须同时覆盖：

- 菜单可见性。
- 按钮/操作权限。
- 后端接口权限。
- 订单数据范围。
- 导出和跨模块信息泄漏控制。

## 菜单权限

| 菜单 | 权限码 | 前端路由 | 后端校验 | 来源 |
| :--- | :--- | :--- | :--- | :--- |
| 订单列表 | `sal:yt:order:list` | `/sales/order` | `/sal/yt/order/list` | qmy-admin / qmy-java |
| 新增订单 | `sal:yt:order:save` | `/sales/order/add` | `/sal/yt/order/saveOrUpdate` | qmy-admin / qmy-java |
| 编辑订单 | `sal:yt:order:save` | `/sales/order/edit` | `/sal/yt/order/saveOrUpdate` | qmy-admin / qmy-java |
| 订单详情 | `sal:yt:order:detail` | `/sales/order/detail` | `/sal/yt/order/listDetail` | qmy-admin / qmy-java |
| 申请采购 | 待补订单侧权限 | `/sales/order/purchase` | `/pur/yt/applyPurchase/saveOrUpdate` | qmy-admin / qmy-java |

## 操作权限

| 操作 | 权限码 | API | 来源状态 |
| :--- | :--- | :--- | :--- |
| 查看列表 | `sal:yt:order:list` | `POST /sal/yt/order/list` | 已声明，且有 `@RequiresDataPermissions` |
| 查看详情 | `sal:yt:order:detail` | `GET /sal/yt/order/listDetail` | 已声明，但仍需订单范围校验 |
| 新增/编辑 | `sal:yt:order:save` | `POST /sal/yt/order/saveOrUpdate` | 已声明，需补数据范围和状态校验 |
| 审核 | `sal:yt:order:audit` | `POST /sal/yt/order/audit` | 已声明，需补审核角色和范围 |
| 设置汇率 | `sal:yt:order:setRate` | `POST /sal/yt/order/setRate` | 已声明，敏感金额操作 |
| 删除 | `sal:yt:order:delete` | `GET /sal/yt/order/delete` | 已声明但 HTTP 方法不安全，需改造 |
| 导出 | `sal:yt:order:export` | `POST /sal/yt/order/export` | 已声明，必须复用数据范围 |
| 商品项退货 | `sal:yt:order:returnItem` | `POST /sal/yt/order/returnItem` | 已声明，需补订单范围和数量校验 |
| 确认发货 | `sal:yt:order:confirmDelivery` | `POST /sal/yt/order/confirmDelivery` | 已声明，需补订单范围和仓储状态校验 |
| 确认完成 | `sal:yt:order:confirmComplete` | `GET /sal/yt/order/confirmComplete` | 已声明但 HTTP 方法不安全，需改造 |
| 修改发货方式 | `sal:yt:order:updateShippingMethod` | `POST /sal/yt/order/updateShippingMethod` | 已声明，需补状态校验 |
| 物流导出 | `sal:yt:order:exportDelivery` | `POST /sal/yt/order/exportDelivery` | 已声明，必须控制收货信息导出 |
| 订单详情聚合 | 待补 | `/detail`、`/subDetail`、`/orderDetail`、`/orderDetailProductTab` | 来源权限注解为空或注释 |
| 订单退货聚合 | 待补 | `/orderReturnItem`、`/returnOrderList`、`/return/statsData`、`/return/returnListBySpec` | 来源权限注解为空或注释 |
| 半成品确认 | 待补 | `/inCompleteList`、`/orderInCompleteList`、`/confirmInComplete` | 来源权限注解为空或注释 |
| 关闭订单 | 待补 | `/close`、`/closePreview` | 来源未声明权限 |
| 物流详情 | 待补 | `/deliveryInfo`、`/packageDetail` | 来源权限注解为空或注释 |
| 导入订单 | 待补 | `/importOrders` | 来源未声明权限 |
| 采购申请保存 | `pur:yt:applyPurchase:saveOrUpdate` | `/pur/yt/applyPurchase/saveOrUpdate` | 采购组件权限，订单侧需校验来源订单范围 |

## 数据范围

| 角色/岗位 | 可见范围 | 可操作范围 | 说明 |
| :--- | :--- | :--- | :--- |
| 普通业务员 | 本人作为业务员或跟单员的订单 | 本人范围内允许状态的订单动作 | 需按 `sale_employee_id`、`follow_employee_id` 或客户归属过滤 |
| 部门负责人 | 本部门业务员/跟单员订单 | 本部门范围内授权动作 | 需对接组织树数据范围 |
| 采购/仓储/财务角色 | 与本岗位待办相关订单片段 | 只操作对应模块动作 | 采购、仓储、财务不应获得订单全部敏感字段 |
| 管理员 | 租户内授权范围 | 按角色授权动作 | 不等同于老板视角 |
| 老板/超级管理员 | 租户全量订单 | 审计允许的全量操作 | 仍需记录操作审计 |

## 后端守卫要求

- `list` 已有数据权限注解，但详情和写接口不能依赖列表结果，必须按订单 ID 重新校验范围。
- 子订单、商品项、退货记录、采购申请必须反查父订单，再做订单范围校验。
- 导出必须使用与列表一致的数据范围条件。
- 状态变更必须校验当前状态、数量、重复动作和跨模块结果。
- 采购申请必须校验订单商品项归属和可采购数量，不能让用户伪造 `orderSubItemId`。
- 物流和包裹详情必须校验订单范围，避免通过仓储 ID 泄漏其他客户收货信息。

## 泄漏检查

- 前端是否隐藏无权限入口：已保留权限码路由，但按钮级权限需接入基座验证。
- 后端是否强制校验权限：来源仅部分接口声明；接入前必须补齐缺口。
- API 是否过滤内部字段：成本、利润、供应商价、回款状态、收货电话应按角色过滤。
- 是否存在跨客户/跨部门读取风险：存在，主要集中在详情、导出、退货、关闭、物流详情和采购申请。
