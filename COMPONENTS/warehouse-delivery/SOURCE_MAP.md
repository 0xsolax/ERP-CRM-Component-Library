# SOURCE_MAP｜warehouse-delivery

## 来源摘要

| 类型 | 路径 | 用途 | 处理方式 |
| :--- | :--- | :--- | :--- |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/warehouse` | 实时库存、入库、发货、物流、箱规页面 | 复制到 `frontend/qmy-admin/` |
| 前端 legacy | `RAW/PROJECTs/qmy-admin/src/views/admin/delivery` | 交货记录、交货列表、操作记录 | 复制到 `frontend/qmy-admin/`，标注为停用入口 |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts` | 仓储发货主 API | 复制到 `frontend/qmy-admin/` |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/admin/delivery/index.ts` | legacy 发货 API | 复制到 `frontend/qmy-admin/` |
| 前端路由 | `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/warehouse.ts` | 启用中的仓储路由和权限码 | 复制到 `frontend/qmy-admin/` |
| 前端路由 | `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/delivery.ts` | 注释状态的 legacy 发货路由 | 复制并标注停用 |
| 前端依赖 | `api/admin/product/index.ts`、`api/admin/sales/customer.ts` | 仓储页面实际使用的产品、客户 API | 裁剪后复制 |
| 前端依赖 | `constant/yitang/warehouse.ts`、`delivery.ts`、`sales.ts`、`product.ts`、`file-type.ts`、`interface/table.ts` | 状态、发货形式、产品状态、上传类型和表格类型 | 复制或裁剪后复制 |
| 前端共享依赖 | `RAW/PROJECTs/qmy-admin/src/hooks/handle/use-handle.ts` | legacy 交货记录删除确认 hook | 未复制，写入依赖边界 |
| 后端 controller | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sto/yt` | 仓储、入库、发货、箱规、物流、库位接口 | 复制到 `backend/qmy-java/` |
| 后端 manager | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sto/yt` | 库存、入库、发货、包裹、运费逻辑 | 复制到 `backend/qmy-java/` |
| 后端数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sto/yt` | 仓储发货实体模型 | 复制到 `backend/qmy-java/` |
| 后端数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/param/sto/yt`、`vo/sto/yt` | 查询和操作参数、发货 VO | 复制到 `backend/qmy-java/` |
| 后端 mapper | `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sto/yt`、`resources/mapper/sto/yt` | 查询、聚合和持久化证据 | 复制到 `backend/qmy-java/` |
| 后端事件 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/event/yt`、`listener/yt` | 库存流水和发货单生成事件 | 复制到 `backend/qmy-java/` |
| 后端任务 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/job/YtStoreWarningJob.java` | 库存预警任务 | 复制到 `backend/qmy-java/` |
| 调研材料 | `RAW/docs/zhongsheng` | 仓储履约相关业务意图 | 复制到 `docs/source/` |

## 抽取范围

已抽取：

- 仓储页面：实时库存、库存历史、占用详情、预警规则、入库列表、入库记录、新增入库。
- 发货页面：发货列表、打包、扫码、包裹、打印预览、物流信息、退回待打包。
- 主 API：库存、入库单、出入库记录、箱规、物流公司、发货单、包裹、扫码、确认发货、运单更新。
- 后端证据：`StoYt*` controller/manager/entity/mapper/event/listener/job/enum/template。
- 调研材料和组件契约文档。

未抽取：

- qmy-java 完整 DDL、索引、唯一键和迁移脚本，当前只写 schema notes。
- 订单、采购、客户、产品、财务模块完整实现。
- qmy-admin 全局布局、请求封装、权限守卫、标签页 store、确认 hook、打印工具、校验工具和共享组件。
- 真实运行环境、账号、数据库和接口回归结果。

## 事实与推断

### 已确认事实

- `warehouse.ts` 是启用中的仓储路由，覆盖 `/warehouse/inventory`、`/warehouse/inbound`、`/warehouse/shipping`、`/warehouse/logistics`、`/warehouse/packing`。
- `delivery.ts` 路由整体注释，`delivery` 前端目录属于 legacy/停用入口。
- qmy-admin 仓储 API 覆盖 `/sto/yt/store/*`、`/sto/yt/store/order/*`、`/sto/yt/box/*`、`/sto/yt/transportCompany/*`、`/sto/yt/delivery/*`。
- qmy-java 存在对应的 `sto/yt` controller/manager/entity/mapper 证据。
- qmy-java controller 中多个接口权限为空或注释，未发现 `@RequiresDataPermissions`。
- 箱规删除和物流公司删除仍使用 GET。

### 推断

- 完整仓储发货必须依赖订单、采购、客户、产品和财务组件，不能作为完全独立业务包运行。
- `sto_yt_store` 同时承载真实库存、可用库存、占用库存、真实在途、可用在途、占用在途，是本组件最关键的一致性模型。
- 库存流水由 `StoreChangeEvent` 和 `StoYtStoreRecord` 承担审计职责，目标项目需要保留同等审计能力。

### 待验证问题

- 订单占用库存和采购占用在途的释放时机。
- 客户独立仓和公共仓之间的库存调拨、发货扣减规则。
- 部分发货、退回待打包、包裹合并后的订单状态回写规则。
- 运费导入、收付款、订单完结由仓储还是财务/订单组件主导。
