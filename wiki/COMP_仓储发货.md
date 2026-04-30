# COMP 仓储发货

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 仓储履约 |
| 复用等级 | 待验证 |
| 适用项目 | 需要库存、入库、打包、物流、发货的项目 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/admin/warehouse`、`RAW/PROJECTs/qmy-admin/src/views/admin/delivery`、`RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts`、`RAW/docs/zhongsheng` |

## 业务目标

管理库存、出入库、箱规、物流公司、打包、发货和发货进度，为订单履约提供仓储执行能力。

## 前端入口

- 仓储：`RAW/PROJECTs/qmy-admin/src/views/admin/warehouse/shipping`、`inventory`、`inbound`、`logistics`、`packing`。
- 发货：`RAW/PROJECTs/qmy-admin/src/views/admin/delivery/list`、`record`、`operate-record`。
- API：`RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts`。

## 后端接口证据

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 库存产品 | POST | `/sto/yt/store/product` | `qmy-admin` API |
| 库存历史 | POST | `/sto/yt/store/history` | `qmy-admin` API |
| 入库/批量入库 | POST | `/sto/yt/store/order/enter*` | `qmy-admin` API |
| 箱规维护 | POST/GET | `/sto/yt/box/*` | `qmy-admin` API |
| 物流公司 | POST/GET | `/sto/yt/transportCompany/*` | `qmy-admin` API |
| 发货列表/详情 | POST | `/sto/yt/delivery/list`、`/detail` | `qmy-admin` API |
| 扫码、确认发货 | POST | `/sto/yt/delivery/scan`、`/confirmDelivery` | `qmy-admin` API |
| 保存/完成打包 | POST | `/sto/yt/delivery/savePackage`、`/completePackage` | `qmy-admin` API |

## 数据结构

本轮未在后端来源中确认仓储完整 SQL。可从前端 API 推断需要：

| 对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| 库存 | 产品/规格、库存数、占用数、库位 | 库存查询和预警 |
| 入库单 | 订单、产品、数量、入库时间 | 入库履约 |
| 箱规 | 箱型、尺寸、装箱数 | 打包和发货 |
| 发货单 | 订单、包裹、物流、发货状态 | 发货执行 |

## 权限边界

- 仓管岗位通常需要库存、入库、打包、发货权限。
- 销售/采购/财务只应看到与自身流程有关的数据，老板视角可全局查看。

## 接入步骤

1. 先确认目标项目是否需要真实库存，还是只需要订单发货记录。
2. 确认库存维度：产品、SKU、规格、批次、库位。
3. 设计库存占用、入库、出库、发货状态。
4. 对齐订单组件的发货触发点。
5. 若需要扫码打包，单独设计包裹和箱规模型。

## 验收清单

- [ ] 库存列表可查询。
- [ ] 入库动作可追踪。
- [ ] 箱规或包裹信息可维护。
- [ ] 发货能关联订单。
- [ ] 发货完成后订单或物流状态同步。

## 已知风险

- 当前仅有 qmy-admin 前端 API 证据，缺少对应后端 SQL 和 controller 证据。
- 仓储是高数据一致性模块，未确认库存扣减规则前不得标记为可直接复用。

