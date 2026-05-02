# COMPONENT_SPEC｜warehouse-delivery

## 组件目标

仓储发货组件负责把订单和采购产生的履约需求转化为可执行的库存、入库、打包、物流、发货和库存流水。组件需要保证库存数值可追溯、发货状态可解释、跨组件边界清晰。

## 核心对象

| 对象 | 职责 | 来源证据 |
| :--- | :--- | :--- |
| 库存 | 记录公共仓真实/可用/占用库存和在途库存 | `StoYtStore` |
| 客户独立仓 | 记录客户维度库存和在途 | `SalYtCustomerStore`，归客户/订单边界 |
| 库存流水 | 记录库存变更前后值、业务类型、订单/采购来源 | `StoYtStoreRecord`、`StoreChangeEvent` |
| 入库单 | 采购入库、独立入库、客户独立仓出入库 | `StoYtStoreOrder` |
| 发货单 | 发货主单、状态、客户、地址、物流和包裹信息 | `StoYtDelivery` |
| 发货明细 | 订单子单商品项、规格、库位、数量和已发数量 | `StoYtDeliveryItem` |
| 包裹 | 箱规、重量、尺寸和包裹内商品 | `StoYtDeliveryBox`、`StoYtDeliveryBoxItem` |
| 箱规 | 打包箱基础资料 | `StoYtBox` |
| 物流公司 | 国际/国内物流公司和上门取件信息 | `StoYtTransportCompany` |

## 状态和流程

### 库存维度

- 公共仓：`realStore`、`enableStore`、`occupyStore`、`realTransit`、`enableTransit`、`occupyTransit`。
- 客户独立仓：`storeNumber`、`transitNumber`、`customerId`、`productId`、`specificationId`、`locationId`。
- 接入前必须明确库位是否进入唯一键，以及同规格多库位如何发货扣减。

### 业务类型

来源 `constant/yitang/warehouse.ts` 和 `StoreEnterOutTypeEnum`：

- 创建订单。
- 创建采购单。
- 采购单入库。
- 独立入库。
- 客户独立仓独立入库。
- 独立出库。
- 客户独立仓独立出库。
- 发货。
- 订单占用可用库存。
- 订单退货。
- 采购单退货。
- 半成品确认。
- 关闭订单释放占用。

### 发货状态

`DeliveryOrderStatusEnum` 与前端发货页面共同表示待打包、待发货、已发货、已完成等状态。目标项目接入时需要补完整状态机：

- 哪些动作允许从待打包进入待发货。
- 部分打包是否可以确认发货。
- 退回待打包是否释放包裹和库存。
- 已发货后是否允许修改物流和运费。
- 发货完成后订单和财务状态如何回写。

## 接入原则

- 库存数值和库存流水必须在同一事务内完成。
- 任何发货扣减必须能追溯到订单、订单子单、商品项、产品规格和库位。
- 任何采购入库必须能追溯到采购单或独立入库来源。
- 库存预警只产生提醒或采购申请边界，不应直接隐式修改库存。
- 前端按钮权限只能控制展示，不能替代后端权限和数据范围守卫。

## 跨组件边界

| 组件 | 边界 |
| :--- | :--- |
| `order-management` | 订单、子订单、商品项、发货触发、订单状态回写 |
| `purchase-supplier` | 采购单、待入库、采购入库、采购退货、库存预警转采购 |
| `product-material` | 产品、规格、图片、箱规关联产品 |
| `customer-management` | 客户、收货地址、客户独立仓 |
| `finance` | 运费导入、收款、付款、利润和对账 |
| `file-oss` | 运单文件、导出文件、图片资源 |

## 草稿限制

- 当前没有正式 DDL 和真实数据库验收。
- 当前没有库存并发扣减测试。
- 当前未补齐 `@RequiresDataPermissions`。
- 当前只做静态快照和契约整理，不做生产可运行包承诺。
