# backend｜order-management

## 分层说明

```text
backend/
  zhongsheng-AI/
    erp-backend/src/main/java/com/erp/
      controller/OrdersController.java
      entity/Orders.java
      mapper/OrdersMapper.java
      service/OrdersService.java
      service/impl/OrdersServiceImpl.java
  qmy-java/
    web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtOrderController.java
    web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java
    service/src/main/java/com/qiaomoyun/manager/sal/yt/
    service/src/main/java/com/qiaomoyun/manager/pur/yt/
    service/src/main/java/com/qiaomoyun/service/sal/yt/SalYtOrderService.java
    entity/src/main/java/com/qiaomoyun/entity/sal/yt/
    entity/src/main/java/com/qiaomoyun/entity/pur/yt/
    entity/src/main/java/com/qiaomoyun/param/sal/yt/
    entity/src/main/java/com/qiaomoyun/vo/sal/yt/
    dao/src/main/java/com/qiaomoyun/mapper/
    dao/src/main/resources/mapper/
    core/src/main/java/com/qiaomoyun/eunm/yt/
```

## legacy 简版订单

来源：`RAW/PROJECTs/zhongsheng-AI`

| 文件 | 能力 |
| :--- | :--- |
| `OrdersController.java` | `/api/orders` 分页、详情、新增、编辑、删除 |
| `Orders.java` | 订单主档字段：编号、客户、来源报价、交期、金额、状态、备注 |
| `OrdersServiceImpl.java` | 基础分页、保存、按主键删除 |
| `OrdersMapper.java` | MyBatis-Plus 基础 Mapper |

适用场景：只需要客户订单主档和明细、状态较简单、没有采购/仓储/财务联动的新项目。

## YT 复杂订单

来源：`RAW/PROJECTs/qmy-java`

| 分层 | 文件 | 能力 |
| :--- | :--- | :--- |
| Controller | `SalYtOrderController.java` | 列表、详情、新增编辑、审核、汇率、退货、半成品、发货、完结、关闭、导出、导入 |
| Controller | `PurYtApplyPurchaseController.java` | 从订单商品项生成采购申请，以及采购申请列表/详情/更换供应商/追加/撤回 |
| Service | `SalYtOrderService.java` | 订单服务接口 |
| Manager | `SalYtOrderManager.java` | 订单主流程、状态流、导出、发货、关闭、退货等业务实现 |
| Manager | `SalYtOrderSubItemOperationManager.java` | 订单商品项操作记录 |
| Manager | `PurYtApplyPurchaseManager.java` | 订单申请采购处理 |
| Entity | `SalYtOrder*`、`SalYtReturnOrder` | 订单主表、子订单、商品项、确认、回款、退货、操作记录 |
| Entity | `PurYtApplyPurchase` | 采购申请桥接表 |
| Mapper | `SalYtOrder*Mapper`、`SalYtReturnOrderMapper` | 订单聚合查询与持久化 |
| Mapper | `PurYtApplyPurchaseMapper` | 采购申请查询与持久化 |
| Enum | `OrderSubItemStatusEnum`、`ShippingMethodEnum` 等 | 状态、发货方式、退货类型 |

## 接入注意

- qmy-java 复杂订单依赖历史项目的租户、权限、数据权限、字典、文件、产品、客户、采购、仓储、财务等上下文，不能直接复制进新后端运行。
- 来源 controller 中多个敏感接口缺少后端权限或数据权限闭环，目标项目接入前必须补齐。
- 订单删除、确认完成使用 GET 是来源事实，不代表推荐做法；新项目应改为符合语义的 DELETE/POST。
- qmy-java 未提供完整 DDL，本组件的复杂表结构只能作为字段契约和迁移设计依据。
- 采购申请只保留订单到采购的边界，后续采购单、入库、付款由采购组件负责。
