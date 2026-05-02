# frontend｜warehouse-delivery

## 快照结构

```text
frontend/qmy-admin/src/
  api/admin/warehouse/index.ts
  api/admin/delivery/index.ts
  api/admin/product/index.ts
  api/admin/sales/customer.ts
  constant/yitang/warehouse.ts
  constant/yitang/delivery.ts
  constant/yitang/sales.ts
  constant/yitang/product.ts
  constant/file-type.ts
  interface/table.ts
  views/admin/router/async-modules/warehouse.ts
  views/admin/router/async-modules/delivery.ts
  views/admin/warehouse/
  views/admin/delivery/
```

## 页面能力

| 页面 | 能力 |
| :--- | :--- |
| `warehouse/inventory/index.vue` | 实时库存列表、分类/产品筛选、预警规则、占用详情 |
| `warehouse/inventory/history.vue` | 库存历史流向 |
| `warehouse/inbound/index.vue` | 入库单列表、入库、批量入库、进度 |
| `warehouse/inbound/add.vue` | 新增独立入库/出库和客户独立仓出入库 |
| `warehouse/inbound/record.vue` | 出入库记录 |
| `warehouse/shipping/index.vue` | 发货列表、打包、包裹、打印、物流信息、退回待打包 |
| `warehouse/logistics/index.vue` | 物流公司维护 |
| `warehouse/packing/index.vue` | 打包箱维护 |
| `delivery/*` | legacy 交货记录页面，来源路由整体注释，默认不启用 |

## API 和常量

- `api/admin/warehouse/index.ts` 是主仓储 API，覆盖库存、入库、箱规、物流、发货和包裹。
- `api/admin/delivery/index.ts` 是 legacy 发货 API，当前路由停用，仅作为历史证据。
- `api/admin/product/index.ts` 已裁剪到 `getCategoryList`、`getProductList`，只用于库存筛选。
- `api/admin/sales/customer.ts` 已裁剪到 `getCustomerList`，只用于新增入库选择客户。
- `constant/yitang/sales.ts` 已裁剪到发货形式标签。
- `constant/yitang/product.ts` 已裁剪到产品状态标签。
- `constant/yitang/warehouse.ts` 提供入库类型、出入库记录类型、物流公司类型、业务类型和发货打包状态。
- `constant/yitang/delivery.ts` 提供 legacy delivery 状态。

## 路由边界

启用入口来自 `warehouse.ts`：

- `/warehouse/inventory`
- `/warehouse/inventory/history`
- `/warehouse/inbound`
- `/warehouse/inbound/record`
- `/warehouse/inbound/add`
- `/warehouse/shipping`
- `/warehouse/logistics`
- `/warehouse/packing`

`delivery.ts` 文件内路由均为注释状态，快照保留用于说明历史交货页面，但不纳入默认装配入口。

## 外部依赖

- `@/layout/admin/index.vue`、路由注册、权限守卫、按钮权限来自 qmy-admin 基座。
- `@/utils/axios`、`@/utils/validate`、`@/utils/print` 来自前端基座。
- `@/views/admin/store/modules/tags`、`@/views/admin/store/modules/permission` 来自 qmy-admin 全局 store。
- `@/hooks/handle/use-handle` 来自 qmy-admin 共享确认 hook，仅 legacy `delivery/record` 使用。
- `@/components/footer-actions/index.vue` 和 `@/components/product-selector/index.vue` 属于共享组件，不作为仓储私有实现复制。
- 产品、客户、订单、采购、财务完整页面不属于本组件。
