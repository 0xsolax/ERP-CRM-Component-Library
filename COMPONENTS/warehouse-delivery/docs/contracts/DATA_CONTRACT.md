# DATA_CONTRACT｜warehouse-delivery

## 数据模型

| 领域 | 表/模型 | 说明 | 接入状态 |
| :--- | :--- | :--- | :--- |
| 库存 | `sto_yt_store` | 公共仓库存、在途、占用、预警 | 有 entity/mapper，缺 DDL |
| 客户仓 | `sal_yt_customer_store` | 客户独立仓库存和在途 | 外部依赖，缺 DDL |
| 库存流水 | `sto_yt_store_record` | 出入库和库存变化审计 | 有 entity/mapper，缺 DDL |
| 入库单 | `sto_yt_store_order` | 入库/出库单据 | 有 entity/mapper，缺 DDL |
| 入库操作 | `sto_yt_store_order_operation` | 入库进度和操作明细 | 有 entity/mapper，缺 DDL |
| 箱规 | `sto_yt_box` | 打包箱规格 | 有 entity/mapper，缺 DDL |
| 物流 | `sto_yt_transport_company` | 物流公司 | 有 entity/mapper，缺 DDL |
| 发货主单 | `sto_yt_delivery` | 发货单、状态、物流和运费 | 有 entity/mapper，缺 DDL |
| 发货明细 | `sto_yt_delivery_item` | 发货产品和订单明细 | 有 entity/mapper，缺 DDL |
| 包裹 | `sto_yt_delivery_box` | 发货包裹 | 有 entity/mapper，缺 DDL |
| 包裹明细 | `sto_yt_delivery_box_item` | 包裹内产品 | 有 entity/mapper，缺 DDL |
| 发货收款 | `sto_yt_delivery_receive` | 发货收款/运费关联 | 有 entity/mapper，缺 DDL |
| 库位 | `StoYtLocation` | 库位 | 未见 `@TableName`，需确认 |

## 库存一致性契约

- 库存变更必须同时写主库存和库存流水。
- 库存扣减不能只按产品维度，必须明确规格、库位、客户仓或公共仓。
- 发货扣减必须能追溯订单商品项和包裹明细。
- 采购入库必须能追溯采购单、采购明细或独立入库单。
- 关闭订单、订单退货、采购退货必须释放或回滚对应占用。
- 库存预警不能绕过人工确认直接生成不可追踪库存变化。

## 字段缺口

- 正式租户隔离字段。
- 软删除和审计字段。
- 业务单号唯一约束。
- 库存并发版本号或行级锁策略。
- 运单文件和 OSS 文件记录关联。
- 付款、收款、运费导入和发货单金额之间的财务对账字段。
