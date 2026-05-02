# backend｜purchase-supplier

## 分层说明

```text
backend/
  zhongsheng-AI/
    erp-backend/src/main/java/com/erp/
      controller/SupplierController.java
      entity/Supplier.java
      mapper/SupplierMapper.java
      service/SupplierService.java
      service/impl/SupplierServiceImpl.java
  qmy-java/
    web/src/main/java/com/qiaomoyun/controller/pur/yt/
    service/src/main/java/com/qiaomoyun/manager/pur/yt/
    entity/src/main/java/com/qiaomoyun/entity/pur/yt/
    entity/src/main/java/com/qiaomoyun/param/pur/yt/
    entity/src/main/java/com/qiaomoyun/vo/pur/yt/
    dao/src/main/java/com/qiaomoyun/mapper/pur/yt/
    dao/src/main/resources/mapper/pur/yt/
```

## legacy 供应商

来源：`RAW/PROJECTs/zhongsheng-AI`

| 文件 | 能力 |
| :--- | :--- |
| `SupplierController.java` | `/api/supplier` 分页、详情、新增、编辑、删除 |
| `Supplier.java` | 供应商编号、名称、联系人、电话、邮箱、地址、状态、备注 |
| `SupplierServiceImpl.java` | 按供应商名称/编号分页查询 |
| `SupplierMapper.java` | MyBatis-Plus 基础 Mapper |

适用场景：只需要供应商基础档案，不需要采购申请、采购单和库存/财务联动。

## YT 采购复杂流

来源：`RAW/PROJECTs/qmy-java`

| 分层 | 文件 | 能力 |
| :--- | :--- | :--- |
| Controller | `PurYtSupplierController.java` | 供应商主档、标签、联系人、跟进、规格对照、采购趋势 |
| Controller | `PurYtApplyPurchaseController.java` | 待采购列表、生成/追加采购、换供应商、撤回申购 |
| Controller | `PurYtPurchaseController.java` | 采购单列表、详情、创建/更新、产品/半成品、退货、跟进、通知、导出、删除暂存 |
| Controller | `PurYtStoreWarningController.java` | 库存预警、申购详情、提交申购、生成预警 |
| Manager | `PurYt*Manager.java` | 采购业务逻辑 |
| Entity | `PurYtSupplier`、`PurYtApplyPurchase`、`PurYtPurchase`、`PurYtPurchaseItem`、`PurYtStoreWarning` 等 | 采购核心对象 |
| Mapper | `PurYt*Mapper` | 采购查询、聚合和持久化 |
| Event/Export | `PurchaseEvent`、`PurchaseEventListener`、`yitang-purchaseExport.xlsx` | 采购事件和导出模板 |

## 接入注意

- qmy-java 复杂采购依赖租户、权限、产品规格、订单商品项、客户、库存、财务和文件模块，不能直接复制进生产。
- 来源 controller 中多个接口权限为空或注释；目标项目必须补齐后端权限和数据范围。
- `follow/delete`、`contact/delete` 等破坏性操作来源仍为 GET，新项目应改为 DELETE/POST。
- qmy-java 未提供完整采购 DDL，本组件只保留实体和 Mapper 反推的字段说明。
- 库存预警、入库、付款和对账必须由仓储/财务组件承接。
