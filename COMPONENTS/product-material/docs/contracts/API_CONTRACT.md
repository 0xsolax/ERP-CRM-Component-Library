# API_CONTRACT｜product-material

## 产品

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新产品 | POST | `/product/saveOrUpdate` | `ProductSaveDTO` | `Long id` | `PRODUCT_SAVE_OR_UPDATE` |
| 分页查询产品 | POST | `/product/page` | `ProductListQueryDTO` | `PageResponse<ProductVO>` | `PRODUCT_PAGE` |
| 产品详情 | POST | `/product/detail` | `IdRequestParam` | `ProductDetailVO` | `PRODUCT_DETAIL` |
| 删除产品 | POST | `/product/delete` | `IdRequestParam` | `Boolean` | `PRODUCT_DELETE` |

## 伞架

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新伞架 | POST | `/umbrellaFrame/saveOrUpdate` | `UmbrellaFrameSaveDTO` | `Long id` | `UMBRELLA_FRAME_SAVE_OR_UPDATE` |
| 分页查询伞架 | POST | `/umbrellaFrame/page` | `UmbrellaFrameListQueryDTO` | `PageResponse<UmbrellaFrameVO>` | `UMBRELLA_FRAME_PAGE` |
| 伞架详情 | POST | `/umbrellaFrame/detail` | `IdRequestParam` | `UmbrellaFrameDetailVO` | `UMBRELLA_FRAME_DETAIL` |
| 删除伞架 | POST | `/umbrellaFrame/delete` | `IdRequestParam` | `Boolean` | `UMBRELLA_FRAME_DELETE` |
| 伞架列表 | POST | `/umbrellaFrame/list` | 空或查询条件 | `List<UmbrellaFrameVO>` | `UMBRELLA_FRAME_LIST` |

## 面料

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新面料 | POST | `/fabric/saveOrUpdate` | `FabricSaveDTO` | `Long id` | `FABRIC_SAVE_OR_UPDATE` |
| 分页查询面料 | POST | `/fabric/page` | `FabricListQueryDTO` | `PageResponse<FabricVO>` | `FABRIC_PAGE` |
| 面料列表 | POST | `/fabric/list` | 空或查询条件 | `List<FabricVO>` | `FABRIC_LIST` |
| 面料详情 | POST | `/fabric/deteil` | `IdRequestParam` | `FabricVO` | `FABRIC_DETAIL` |
| 删除面料 | POST | `/fabric/delete` | `IdRequestParam` | `Boolean` | `FABRIC_DELETE` |

说明：`/fabric/deteil` 为来源事实。新项目建议兼容旧路径并新增 `/fabric/detail`，或统一迁移前端。

## 材料

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存材料分类 | POST | `/material/category/saveOrUpdate` | `MaterialCategorySaveDTO` | `Long id` | `MATERIAL_CATEGORY_SAVE` |
| 材料分类列表 | POST | `/material/category/list` | 空 | `List<MaterialCategoryVO>` | `MATERIAL_CATEGORY_LIST` |
| 删除材料分类 | POST | `/material/category/delete` | `IdRequestParam` | `Boolean` | `MATERIAL_CATEGORY_DELETE` |
| 保存或更新材料 | POST | `/material/saveOrUpdate` | `MaterialSaveDTO` | `Long id` | `MATERIAL_SAVE_OR_UPDATE` |
| 分页查询材料 | POST | `/material/page` | `MaterialListQueryDTO` | `PageResponse<MaterialVO>` | `MATERIAL_PAGE` |
| 删除材料 | POST | `/material/delete` | `IdRequestParam` | `Boolean` | `MATERIAL_DELETE` |
| 按分类查询材料 | POST | `/material/listByCategoryId` | `IdRequestParam` | `List<MaterialSimpleVO>` | `MATERIAL_LIST_BY_CATEGORY` |

## 包材

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新包材 | POST | `/packaging/saveOrUpdate` | `PackagingSaveDTO` | `Long id` | `PACKAGING_SAVE_OR_UPDATE` |
| 批量保存默认纸箱 | POST | `/packaging/saveOrUpdateDefaultPaperBox` | `List<PackagingSaveDTO>` | `List<Long>` | `PACKAGING_SAVE_DEFAULT` |
| 分页查询包材 | POST | `/packaging/page` | `PackagingListQueryDTO` | `PageResponse<PackagingVO>` | `PACKAGING_PAGE` |
| 删除包材 | POST | `/packaging/delete` | `IdRequestParam` | `Boolean` | `PACKAGING_DELETE` |

前端待核对：

| 前端封装 | 状态 | 处理建议 |
| :--- | :--- | :--- |
| `/packaging/typeList` | 当前后端快照未找到 controller | 新项目补齐或改为 `base-data` 的包材类型查询 |
| `/box-price/list` | 当前后端快照未找到 controller | 若不做独立纸箱价格表，应删除前端封装 |

## 工序

| 能力 | 方法 | 路径 | 入参 | 返回 | 权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 保存或更新工序 | POST | `/process/saveOrUpdate` | `ProcessSaveDTO` | `Long id` | `PROCESS_SAVE_OR_UPDATE` |
| 分页查询工序 | POST | `/process/page` | `ProcessListQueryDTO` | `PageResponse<ProcessVO>` | `PROCESS_PAGE` |
| 工序列表 | POST | `/process/list` | 空或查询条件 | `List<ProcessVO>` | `PROCESS_LIST` |
| 删除工序 | POST | `/process/delete` | `IdRequestParam` | `Boolean` | `PROCESS_DELETE` |

## 前端 API

| 文件 | 对应后端 |
| :--- | :--- |
| `frontend/qmy-admin/src/api/zs/product/index.ts` | `/product/*` |
| `frontend/qmy-admin/src/api/zs/material/umbrella-frame.ts` | `/umbrellaFrame/*` |
| `frontend/qmy-admin/src/api/zs/material/fabric.ts` | `/fabric/*` |
| `frontend/qmy-admin/src/api/zs/material/material.ts` | `/material/*` |
| `frontend/qmy-admin/src/api/zs/material/packaging.ts` | `/packaging/*`、待验证 `/box-price/list` |
| `frontend/qmy-admin/src/api/zs/base-info/process.ts` | `/process/*` |
