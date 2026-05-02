# frontend｜order-management

## 快照结构

```text
frontend/qmy-admin/src/
  api/admin/sales/order.ts
  api/admin/sales/customer.ts
  api/admin/auth/org.ts
  api/admin/product/index.ts
  constant/yitang/sales.ts
  constant/yitang/finance.ts
  views/admin/router/async-modules/sales.ts
  views/admin/sales/order/
    index.vue
    add.vue
    edit.vue
    detail.vue
    purchase.vue
    components/
```

## 页面能力

| 页面/组件 | 能力 |
| :--- | :--- |
| `index.vue` | 订单列表、筛选、状态、审核、删除、导出、退货、完结、关闭、物流入口 |
| `add.vue` | 新增订单入口 |
| `edit.vue` | 编辑订单入口 |
| `detail.vue` | 订单主档、子订单、商品项、退货、发货、物流、关闭等详情 |
| `purchase.vue` | 订单商品项申请采购 |
| `order-form.vue` | 订单表单、客户、产品、规格、地址、发货方式、入库发货等核心录入 |
| `refund-dialog.vue`、`refund-detail-dialog.vue` | 订单退货和退货明细 |
| `confirm-shipment-dialog.vue` | 确认发货 |
| `edit-shipping-method-dialog.vue` | 修改发货方式 |
| `export-dialog.vue` | 导出参数 |
| `logistics-detail-dialog.vue` | 物流/包裹展示边界 |
| `product-progress.vue` | 商品项进度展示 |

## API 和常量

- `api/admin/sales/order.ts` 是订单页面的主 API 封装，覆盖 `/sal/yt/order/*` 与 `/pur/yt/applyPurchase/saveOrUpdate`。
- `api/admin/sales/customer.ts` 用于订单表单选择客户、地址等客户数据。
- `api/admin/product/index.ts` 用于订单商品、规格、库存/在途等产品数据。
- `api/admin/auth/org.ts` 用于业务员、跟单员等组织用户选择。
- `constant/yitang/sales.ts` 提供订单状态、订单类型、币种、发货方式、操作类型。
- `constant/yitang/finance.ts` 提供应收/利润状态展示。

## 路由裁剪

来源 `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/sales.ts` 包含客户、订单、独立仓历史等销售入口。本组件快照已裁剪为订单相关路由：

- `/sales/order`
- `/sales/order/add`
- `/sales/order/edit`
- `/sales/order/detail`
- `/sales/order/purchase`

未保留客户列表、报价、独立仓历史等非订单入口，避免快照装配时引用未包含页面。

## 外部依赖

- `@/layout/admin/index.vue`、路由注册、权限守卫、按钮权限来自 qmy-admin 基座。
- `@/utils/download`、`@/utils/auth`、请求封装、全局 Element Plus 组件来自前端基座。
- 客户、产品、采购、仓储、财务页面不属于本组件，但订单页面会通过 API 或字段展示这些模块的结果。

## 接入注意

- 前端 `hidden` 或按钮权限只是展示控制，不能替代后端接口权限。
- 订单详情、退货、发货、关闭、导出会展示价格、客户、收货、库存和财务信息，必须等后端权限与数据范围补齐后开放。
- 申请采购页面只能生成采购申请，不能把采购单完整流转并入订单组件。
- 若目标项目只采用简版订单 CRUD，应重做轻量页面，不建议直接接入 YT 复杂订单页面。
