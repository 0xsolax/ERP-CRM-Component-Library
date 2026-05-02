# COMP 仓储发货

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 仓储履约 |
| 复用等级 | 草稿 |
| 当前快照 | `COMPONENTS/warehouse-delivery` |
| 适用项目 | 需要库存、入库、打包、物流、发货、出入库流水的项目 |
| 关键来源 | `RAW/PROJECTs/qmy-admin/src/views/admin/warehouse`、`RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts`、`RAW/PROJECTs/qmy-java/.../sto/yt`、`RAW/docs/zhongsheng` |

## 业务目标

管理公共仓和客户独立仓的库存、在途、占用、入库、箱规、物流公司、打包、发货和库存流水，为订单履约和采购入库提供仓储执行能力。

## 快照内容

- 前端页面：实时库存、库存历史、占用详情、预警规则、入库列表、入库记录、新增入库、发货列表、打包、包裹、打印、物流公司、打包箱。
- 前端 API：`/sto/yt/store/*`、`/sto/yt/store/order/*`、`/sto/yt/box/*`、`/sto/yt/transportCompany/*`、`/sto/yt/delivery/*`。
- 后端代码：`StoYtStore`、`StoYtStoreOrder`、`StoYtDelivery`、`StoYtBox`、`StoYtTransportCompany`、`StoYtLocation` 的 controller/manager/entity/mapper。
- 库存事件：`StoreChangeEvent`、`StoreEventListener`、`DeliveryEvent`、`DeliveryEventListener`。
- 库存预警任务：`YtStoreWarningJob`。
- 文档：API、数据、权限、验收证据和 schema notes。

## 关键数据口径

| 领域 | 口径 |
| :--- | :--- |
| 公共仓 | 真实库存、可用库存、占用库存、真实在途、可用在途、占用在途 |
| 客户独立仓 | 客户、产品、规格、库位、库存数、在途数 |
| 入库 | 采购入库、独立入库、客户独立仓独立入库 |
| 出库 | 独立出库、客户独立仓独立出库、发货扣减 |
| 流水 | 创建订单、创建采购单、入库、出库、发货、退货、半成品确认、关闭订单释放占用 |

## 权限边界

- 前端路由使用 `sto:yt:store:list`、`sto:yt:order:list`、`sto:yt:order:addStore`、`sto:yt:delivery:list`、`sto:yt:transportCompany:list`、`sto:yt:box:list`。
- qmy-java controller 中多个接口权限为空或注释，且未发现 `@RequiresDataPermissions`。
- `GET /sto/yt/box/delete`、`GET /sto/yt/transportCompany/delete/{id}` 属于破坏性 GET，接入前必须收口。

## 接入顺序

1. 先接入产品、客户、订单、采购、认证权限基座。
2. 确认库存维度和客户独立仓是否启用。
3. 补正式 DDL、租户字段、索引、唯一键、库存流水审计。
4. 补数据范围权限和状态变更幂等校验。
5. 用真实订单、采购、入库、打包、发货链路做库存一致性回归。

## 验收清单

- [x] 库存、入库、箱规、物流、打包、发货前端 API 能力清单已完整整理。
- [x] qmy-java 后端 controller/manager/entity/mapper 证据已带出。
- [x] 后端 SQL/controller 缺口已列出：缺正式 DDL、权限闭环、破坏性 GET 收口、运行验收。
- [x] 未确认库存扣减规则前保持 `status: draft`。
- [ ] 正式接入时完成库存并发扣减和占用释放测试。

## 已知风险

- qmy-java 有代码证据但没有完整迁移 SQL，不能直接上线。
- 库存是高一致性模块，任何订单关闭、采购退货、发货退回都可能影响占用和流水。
- `StoYtDeliveryManager` 与订单、客户、产品、财务、飞书等模块耦合明显，接入时需要拆清责任边界。
