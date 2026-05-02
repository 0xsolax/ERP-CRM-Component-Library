# frontend｜purchase-supplier

## 快照结构

```text
frontend/qmy-admin/src/
  api/admin/purchase/
    supplier.ts
    pending.ts
    purchased.ts
    stock-warning.ts
  views/admin/purchase/
    supplier/
    pending/
    purchased/
    stock-warning/
  views/admin/router/async-modules/purchase.ts
  constant/yitang/purchase.ts
```

## 页面能力

| 页面/组件 | 能力 |
| :--- | :--- |
| `purchase/supplier/index.vue` | 供应商列表 |
| `purchase/supplier/add.vue` | 新增供应商 |
| `purchase/supplier/detail.vue` | 供应商详情、联系人、标签、跟进、产品规格对照 |
| `purchase/pending/index.vue` | 待采购列表、撤回申购 |
| `purchase/pending/generate-order.vue` | 待采购生成采购单 |
| `purchase/pending/components/*` | 追加采购、更换供应商 |
| `purchase/purchased/index.vue` | 已采购列表、导出 |
| `purchase/purchased/add.vue` | 新增/编辑采购单、暂存删除 |
| `purchase/purchased/detail.vue` | 采购单详情、采购产品、半成品、跟进、退货、通知供应商 |
| `purchase/stock-warning/index.vue` | 库存预警列表 |
| `purchase/stock-warning/batch-apply.vue` | 库存预警批量申购 |

## API 和常量

- `api/admin/purchase/supplier.ts` 覆盖供应商主档、标签、联系人、跟进、规格对照、采购趋势/占比。
- `api/admin/purchase/pending.ts` 覆盖待采购、换供应商、可更换供应商、暂存采购单、追加采购、生成采购单、撤回申购。
- `api/admin/purchase/purchased.ts` 覆盖采购单列表、详情、产品/半成品、退货、跟进、通知、删除暂存。
- `api/admin/purchase/stock-warning.ts` 覆盖库存预警、申购详情、提交申购。
- `constant/yitang/purchase.ts` 提供采购状态、付款方式和半成品状态。
- `constant/yitang/sales.ts` 被采购商品进度组件用于采购操作类型标签。

## 路由

保留来源采购路由：

- `/purchase/stock-warning`
- `/purchase/stock-warning/batch-apply`
- `/purchase/pending`
- `/purchase/pending/generate-order`
- `/purchase/purchased`
- `/purchase/purchased/add`
- `/purchase/purchased/detail`
- `/purchase/supplier`
- `/purchase/supplier/add`
- `/purchase/supplier/detail`

## 外部依赖

- qmy-admin 布局、请求封装、权限守卫、下载工具、上传组件、标签页 store、表格类型来自前端基座。
- 产品选择、供应商规格、库存/在途来自 `product-material` 和 `warehouse-delivery`。
- 客户和订单来源来自 `customer-management` 与 `order-management`。
- 付款、应付、对账来自 `finance`。

## 接入注意

- 采购页面较重，不适合作为只需要供应商 CRUD 的项目默认入口。
- 前端按钮和路由权限不能替代后端权限，尤其是采购单导出、退货、通知供应商、换供应商。
- 导出使用请求头 `qiaomoyun-token`，这是 token header 名称，不是实际密钥。
