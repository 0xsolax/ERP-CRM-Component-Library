# warehouse-delivery｜仓储发货组件

## 定位

`warehouse-delivery` 是库存、入库、箱规、物流公司、打包、发货和出入库流水的业务组件草稿。当前快照用于还原 qmy-admin/qmy-java 的仓储履约能力，不代表可以直接复制上线。

组件状态为 `draft`。原因是 qmy-java 有较完整的 Java 代码证据，但缺正式 DDL、运行验收、数据范围权限闭环和库存扣减规则确认。

## 已抽取内容

- qmy-admin 仓储前端：实时库存、历史流向、入库列表、入库记录、新增入库、发货列表、物流公司、打包箱管理。
- qmy-admin legacy 发货页面：交货记录、交货列表、操作记录。该路由文件已整体注释，默认不作为启用入口。
- qmy-admin API：`/sto/yt/store/*`、`/sto/yt/store/order/*`、`/sto/yt/box/*`、`/sto/yt/transportCompany/*`、`/sto/yt/delivery/*`。
- qmy-java 后端：`StoYtStore`、`StoYtStoreOrder`、`StoYtDelivery`、`StoYtBox`、`StoYtTransportCompany`、`StoYtLocation` 的 controller/manager/entity/param/VO/mapper。
- 库存事件：`StoreChangeEvent`、`StoreEventListener`、`DeliveryEvent`、`DeliveryEventListener`。
- 库存预警任务：`YtStoreWarningJob`。
- 导出/导入模板：`yitang-deliveryExport.xlsx`、`ytiang-customerDeliveryCostImport.xlsx`。
- 调研材料：`RAW/docs/zhongsheng`。

## 业务链路

1. 订单创建后占用库存或在途库存。
2. 采购或独立入库产生入库单和库存流水。
3. 仓储根据发货单进行打包、扫码、包裹保存和完成打包。
4. 发货时维护物流公司、运单、面单和发货状态。
5. 库存变更通过事件记录到 `sto_yt_store_record`。
6. 运费导入、收付款、订单完结由财务和订单组件承接。

## 接入顺序

1. 先接入基座、认证权限、产品物料、客户、订单和采购组件。
2. 明确库存维度：公共仓/客户独立仓、产品、规格、库位、在途、占用、可用。
3. 建立正式 DDL、索引、唯一键、软删除、租户字段和库存流水审计字段。
4. 补齐接口权限、数据范围守卫和破坏性接口 HTTP 方法。
5. 用真实订单、采购、入库、发货数据做库存一致性回归。

## 不能直接复用的原因

- 未找到 qmy-java 仓储发货完整 SQL，表结构只能从 entity/mapper 推导。
- 多个 controller 接口权限为空或注释，且未发现 `@RequiresDataPermissions`。
- `box/delete`、`transportCompany/delete/{id}` 是破坏性 GET。
- 库存扣减、占用释放、客户独立仓和公共仓之间的规则需要业务确认。
- `StoYtDeliveryManager` 与订单、财务、飞书、产品文件等模块耦合明显，目标项目需要拆边界后接入。

## 关键文档

- [SOURCE_MAP.md](SOURCE_MAP.md)
- [组件规范](docs/spec/COMPONENT_SPEC.md)
- [API 契约](docs/contracts/API_CONTRACT.md)
- [数据契约](docs/contracts/DATA_CONTRACT.md)
- [权限契约](docs/contracts/PERMISSION_CONTRACT.md)
- [验收证据](docs/acceptance/REVIEW_EVIDENCE.md)
