# 前端快照说明

## 已复制入口

| 类型 | 快照路径 | 说明 |
| :--- | :--- | :--- |
| 报价页面 | `src/views/sed/sales/quotation` | 报价列表和所有报价弹窗组件 |
| 报价 API | `src/api/sed/sales/quotation.ts` | `/sal/sed/quotation/*` 前端封装 |
| 依赖 API | `src/api/sed/product/packing.ts`、`src/api/admin/sales/customer.ts` | 包材选择、新增包材名称和客户下拉，已裁剪到报价页实际使用函数 |
| 常量 | `src/constant/sed/quotation.ts`、`src/constant/sed/sales.ts`、`src/constant/file-type.ts` | 报价状态、操作记录、币种、税、FOB、EXW、订单来源、图片类型 |
| 类型 | `src/interface/table.ts` | `ColumnProps` 等表格类型 |
| 路由 | `src/views/sed/router/async-modules/sales.ts` | 销售管理入口，快照已裁剪为报价路由 |

## 页面组件

- `index.vue`：报价列表、筛选、提交审核、导出和弹窗调度。
- `quotation-dialog.vue`：新增、编辑、再次创建报价单，选择客户、产品 SKU、包材、历史报价导入。
- `detail-dialog.vue`：报价详情、成本、历史操作、审核、会签、总裁微信审核。
- `cost-detail-drawer.vue`：配件、工艺、包材等成本明细。
- `history-price-drawer.vue`：历史报价、均价、中位数、毛利率和趋势。
- `procurement-cost-dialog.vue`、`logistics-cost-dialog.vue`：采购与物流成本确认。
- `convert-order-dialog.vue`、`merge-to-order-dialog.vue`：整单、单 SKU、合并转订单。
- `export-dialog.vue`：报价导出。
- `president-wx-audit-dialog.vue`：总裁微信审核凭证。

## 基座依赖

该目录不是独立前端应用，仍依赖目标基座提供：

- `@/layout/sed/index.vue`、`@/views/sed/store/modules/user`。
- `@/components/sed-product-selector`、`@/components/remote-autocomplete`。
- `@/utils/axios`、`@/utils/download`、`@/utils/auth`、`@/utils/validate`。
- Element Plus、全局权限指令、表格/弹窗样式和用户信息。

## 接入注意

- `sales.ts` 来源同时含客户和订单路由，快照中已裁剪为 `/sales/quotation`，客户和订单入口由对应组件提供。
- 报价页会读取客户、产品和包材数据，新项目必须先完成这些依赖组件。
- 历史报价和成本明细展示价格、成本和毛利率，应与后端权限一起控制。
