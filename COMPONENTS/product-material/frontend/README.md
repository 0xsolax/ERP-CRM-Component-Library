# frontend｜product-material

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `qmy-admin/src/views/zs/base-info/product` | 产品列表、新增、编辑、产品表单 |
| `qmy-admin/src/views/zs/base-info/process` | 工价管理 |
| `qmy-admin/src/views/zs/material/umbrella-frame` | 伞架管理 |
| `qmy-admin/src/views/zs/material/fabric` | 面料管理 |
| `qmy-admin/src/views/zs/material/material` | 材料分类和其他材料 |
| `qmy-admin/src/views/zs/material/packaging` | 包材管理和纸箱单价弹窗 |
| `qmy-admin/src/api/zs/product` | 产品 API |
| `qmy-admin/src/api/zs/material` | 伞架、面料、材料、包材 API |
| `qmy-admin/src/api/zs/base-info/process.ts` | 工序 API |
| `qmy-admin/src/views/zs/router/async-modules` | 产品、工序、材料路由 |

## 依赖

- `base-data`：字段下拉、产品类型、面料种类/型号/门幅、包材类型、伞架规格。
- `file-oss`：产品、材料、伞架图片上传组件。
- 目标前端基座：`@/utils/axios`、`IResponseModel`、布局、弹窗、Element Plus、路由守卫。

## 快照整理

- 路由权限已从来源占位 `sys:role:list` 替换为产品/材料/工序权限。
- 页面仍保留来源项目的业务字段和 UI 交互，接入新项目时应先对齐目标行业。
- `packaging.ts` 中 `/packaging/typeList`、`/box-price/list` 需要后端补齐或清理。
