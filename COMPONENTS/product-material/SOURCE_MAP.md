# SOURCE_MAP｜product-material

## 来源摘要

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product` | 产品主档、产品 BOM、产品图片、成本字段、分页筛选 |
| `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material` | 伞架、面料、材料、材料分类、包材 |
| `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/process` | 工序工价 |
| `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto` | product/material/process DTO |
| `RAW/PROJECTs/zhongsheng-backend/docs/sql` | 产品、物料、工序 SQL |
| `RAW/PROJECTs/qmy-admin/src/views/zs/base-info` | 产品、工价页面 |
| `RAW/PROJECTs/qmy-admin/src/views/zs/material` | 伞架、面料、材料、包材页面 |
| `RAW/PROJECTs/qmy-admin/src/api/zs` | 前端 API 封装 |
| `RAW/docs/zhongsheng` | 产品、BOM、包材成本、报价边界调研资料 |

## 已复制范围

### zhongsheng-backend

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-core/.../core/product` | `backend/zhongsheng-backend/zhongsheng-core/.../core/product` | 产品 controller/service/manager/dao/entity/VO/condition |
| `zhongsheng-core/.../core/material` | `backend/zhongsheng-backend/zhongsheng-core/.../core/material` | 伞架、面料、材料、材料分类、包材 |
| `zhongsheng-core/.../core/process` | `backend/zhongsheng-backend/zhongsheng-core/.../core/process` | 工序工价 |
| `zhongsheng-api/.../dto/product` | `backend/zhongsheng-backend/zhongsheng-api/.../dto/product` | 产品保存、列表、BOM 子项 DTO |
| `zhongsheng-api/.../dto/material` | `backend/zhongsheng-backend/zhongsheng-api/.../dto/material` | 伞架、面料、材料、包材 DTO |
| `zhongsheng-api/.../dto/process` | `backend/zhongsheng-backend/zhongsheng-api/.../dto/process` | 工序 DTO |
| `zhongsheng-common/.../ApiPermissionConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../ApiPermissionConstants.java` | 产品、材料、工序权限码 |
| `zhongsheng-common/.../error/*` | `backend/zhongsheng-backend/zhongsheng-common/.../error` | 产品、材料、面料、包材、伞架、工序错误码 |
| `zhongsheng-common/.../BaseTreeNodeSeedEnum.java` | `backend/zhongsheng-backend/zhongsheng-common/.../enums/BaseTreeNodeSeedEnum.java` | 包材默认纸箱依赖的基础数据节点 |
| `docs/sql/init-product.sql` | `db/zhongsheng-backend/init-product.sql` | 产品主表和产品 BOM 表 |
| `docs/sql/init-material.sql` | `db/zhongsheng-backend/init-material.sql` | 材料分类和其他材料 |
| `docs/sql/init-fabric.sql` | `db/zhongsheng-backend/init-fabric.sql` | 面料 |
| `docs/sql/init-packaging.sql` | `db/zhongsheng-backend/init-packaging.sql` | 包材 |
| `docs/sql/init-process.sql` | `db/zhongsheng-backend/init-process.sql` | 工序 |
| `docs/sql/init-umbrella-frame.sql` | `db/zhongsheng-backend/init-umbrella-frame.sql` | 伞架和伞架材料绑定 |

### qmy-admin

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `src/views/zs/base-info/product` | `frontend/qmy-admin/src/views/zs/base-info/product` | 产品列表、新增、编辑、产品表单 |
| `src/views/zs/base-info/process` | `frontend/qmy-admin/src/views/zs/base-info/process` | 工价管理 |
| `src/views/zs/material/fabric` | `frontend/qmy-admin/src/views/zs/material/fabric` | 面料管理 |
| `src/views/zs/material/material` | `frontend/qmy-admin/src/views/zs/material/material` | 材料分类和其他材料 |
| `src/views/zs/material/packaging` | `frontend/qmy-admin/src/views/zs/material/packaging` | 包材和纸箱单价维护 |
| `src/views/zs/material/umbrella-frame` | `frontend/qmy-admin/src/views/zs/material/umbrella-frame` | 伞架维护 |
| `src/api/zs/product/index.ts` | `frontend/qmy-admin/src/api/zs/product/index.ts` | 产品 API |
| `src/api/zs/material/*` | `frontend/qmy-admin/src/api/zs/material` | 材料、面料、包材、伞架 API |
| `src/api/zs/base-info/process.ts` | `frontend/qmy-admin/src/api/zs/base-info/process.ts` | 工序 API |
| `src/views/zs/router/async-modules/base-info.ts` | `frontend/qmy-admin/src/views/zs/router/async-modules/base-info.ts` | 产品/工序路由入口，快照中已替换权限 |
| `src/views/zs/router/async-modules/material.ts` | `frontend/qmy-admin/src/views/zs/router/async-modules/material.ts` | 材料管理路由入口，快照中已替换权限 |

## 未复制或作为依赖处理

| 内容 | 处理 | 原因 |
| :--- | :--- | :--- |
| `core/base`、`dto/base`、`init-base-data.sql` | 依赖 `base-data` | 字段/分类是基础组件职责 |
| `core/file`、`dto/file`、`init-system-file.sql` | 依赖 `file-oss` | 产品/材料图片通过文件组件保存 |
| `application-*.yml`、`.env` | 未复制 | 环境配置和密钥风险 |
| `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/` | 未复制 | 污染文件或构建缓存 |
| `RAW/docs/zhongsheng` 原会议全文 | 未整包复制 | 原文较长且跨多个业务组件，本组件只整理来源摘要和索引 |

## 快照中已做的整理

- 前端路由权限由来源占位值 `sys:role:list` 替换为：
  - 产品：`product:product:page`、`product:product:save`
  - 工序：`process:process:page`
  - 伞架：`material:umbrella:page`
  - 面料：`material:fabric:page`
  - 材料：`material:material:page`
  - 包材：`material:packaging:page`
- 保留来源事实 `/fabric/deteil`，并在快照后端新增 `/fabric/detail` 标准路径兼容。
- 移除来源前端未使用的 `/packaging/typeList`、`/box-price/list` API 封装；包材类型由 `base-data` 查询，纸箱单价列表由 `/packaging/page` 加 `defaultTypeFlag = 1` 查询。

## 事实与判断

- `product` 是产品主档，`product_*` 关系表保存产品组成和成本来源快照。
- `umbrella_frame`、`fabric`、`material`、`packaging`、`process` 是产品 BOM 可引用的主档。
- 多个规格字段通过 `base_data` ID 输入，并在业务表里保存名称快照。
- 图片字段不直接进产品表，而通过 `system_file` 的 `mainType + subType + masterId` 关联。
- `product.total_cost` 和 `product.selling_price` 属于产品主档参考值；客户、数量、汇率、税费、利润、折扣和历史报价应由报价组件保存时点快照。
- 包材成本在调研资料中存在“固定单价”和“按纸箱尺寸/层数/数量估算”两类口径，新项目必须在报价阶段单独确认。
