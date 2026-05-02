# backend｜warehouse-delivery

## 快照结构

```text
backend/qmy-java/
  web/src/main/java/com/qiaomoyun/controller/sto/yt/
  service/src/main/java/com/qiaomoyun/manager/sto/yt/
  service/src/main/java/com/qiaomoyun/event/yt/
  service/src/main/java/com/qiaomoyun/listener/yt/
  service/src/main/java/com/qiaomoyun/job/YtStoreWarningJob.java
  entity/src/main/java/com/qiaomoyun/entity/sto/yt/
  entity/src/main/java/com/qiaomoyun/param/sto/yt/
  entity/src/main/java/com/qiaomoyun/vo/sto/yt/
  dao/src/main/java/com/qiaomoyun/mapper/sto/yt/
  dao/src/main/resources/mapper/sto/yt/
  core/src/main/java/com/qiaomoyun/eunm/yt/
```

## 后端能力

| 能力 | Controller | Manager | 说明 |
| :--- | :--- | :--- | :--- |
| 实时库存 | `StoYtStoreController` | `StoYtStoreManager` | 库存查询、库存历史、占用详情、预警规则 |
| 入库单 | `StoYtStoreOrderController` | `StoYtStoreOrderManager`、`StoYtStoreManager` | 新增入库、采购入库、分配订单入库、批量入库 |
| 发货单 | `StoYtDeliveryController` | `StoYtDeliveryManager` | 发货列表、详情、打包、扫码、确认发货、物流信息、退回待打包 |
| 箱规 | `StoYtBoxController` | `StoYtBoxManager` | 箱型维护、详情、删除、下拉 |
| 物流公司 | `StoYtTransportCompanyController` | `StoYtTransportCompanyManager` | 物流公司增删改查 |
| 库位 | `StoYtLocationController` | `StoYtLocationManager` | 库位下拉和新增 |
| 库存流水 | 事件监听 | `StoreEventListener` | 根据业务事件生成 `sto_yt_store_record` |
| 发货生成 | 事件监听 | `DeliveryEventListener` | 根据订单商品项生成或同步发货单 |

## 数据一致性注意

- `StoYtStore` 同时记录公共仓真实库存、可用库存、占用库存、真实在途、可用在途、占用在途。
- `SalYtCustomerStore` 代表客户独立仓，当前未复制完整客户/销售模块，只在契约中标注为依赖。
- `StoYtStoreManager` 内存在库存加减、占用释放、采购入库、发货扣减等多类入口，接入时必须做同一事务边界和并发一致性复核。
- `StoreChangeEvent` 是库存流水审计入口，不能只迁移库存数值而丢弃流水。

## 已知缺口

- 未找到 qmy-java 仓储完整 DDL。
- 多个接口权限注解为空或注释，且未发现数据范围注解。
- 破坏性操作仍有 GET：`/sto/yt/box/delete`、`/sto/yt/transportCompany/delete/{id}`。
- `StoYtDeliveryManager` 引用订单、客户独立仓、产品、财务、飞书等模块，目标项目需要按组件边界拆分。
- 当前快照未执行后端编译和接口回归。
