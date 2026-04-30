# PROJECT qmy-admin

## 定位

`qmy-admin` 是 Vue 3 + Vite + TypeScript + Element Plus 的多租户管理后台来源项目。它对 ERP/CRM 组件库的主要价值是前端页面结构、路由组织、API 封装、通用表格/搜索组件和多租户模块拆分方式。

## 来源路径

- `RAW/PROJECTs/qmy-admin/README.md`
- `RAW/PROJECTs/qmy-admin/package.json`
- `RAW/PROJECTs/qmy-admin/src/views`
- `RAW/PROJECTs/qmy-admin/src/api`
- `RAW/PROJECTs/qmy-admin/src/components`
- `RAW/PROJECTs/qmy-admin/src/utils/axios`

## 技术栈事实

- Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router、Axios。
- README 中明确 `src/api` 按租户/业务模块分目录，`src/views` 按租户和模块分目录。
- 全局组件包含 `bz-table`、`search-form`、`bz-upload`、`bz-select-filter`、`bz-tree-filter` 等。

## 可贡献组件

| 组件 | 证据 | 复用价值 |
| :--- | :--- | :--- |
| 登录认证与权限 | `src/views/*/login`、`src/api/*/auth`、`permission.ts` | 登录页、路由守卫、菜单权限、用户信息接口 |
| 客户管理 | `src/views/admin/sales/customer`、`src/api/admin/sales/customer.ts`、`src/views/sed/sales/customer` | 客户列表、详情、联系人、地址、跟进、标签 |
| 报价管理 | `src/views/sed/sales/quotation`、`src/api/sed/sales/quotation.ts` | 报价列表、成本确认、审核、历史报价、转订单 |
| 订单管理 | `src/views/admin/sales/order`、`src/api/admin/sales/order.ts` | 订单列表、详情、审核、采购申请、退货、完结 |
| 产品物料基础数据 | `src/views/zs/base-info`、`src/views/zs/material`、`src/api/zs` | 产品、伞架、面料、包材、工序、字段管理 |
| 采购供应商 | `src/views/admin/purchase`、`src/api/admin/purchase` | 采购申请、采购单、供应商、库存预警 |
| 仓储发货 | `src/views/admin/warehouse`、`src/views/admin/delivery`、`src/api/admin/warehouse/index.ts` | 入库、库存、物流、打包、发货 |
| 文件上传与 OSS | `src/components/bz-upload`、`src/api/admin/system/storage.ts` | 上传组件与文件存储记录 |

## 待验证

- `qmy-admin` 的多个租户模块使用不同接口前缀，不能直接当成统一后端契约。
- 页面级能力完整，但部分后端实现不在本仓同一来源内，需要与 `project-scaffold` 或 `zhongsheng-backend` 对齐。

