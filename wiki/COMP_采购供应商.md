# COMP 采购供应商

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 采购 |
| 快照路径 | `COMPONENTS/purchase-supplier` |
| 复用等级 | `draft`，待验证 |
| 适用项目 | 供应商管理、订单采购、库存预警申购、采购跟进、采购退货 |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 业务目标

维护供应商档案，并在订单或库存预警产生采购需求后，按供应商聚合采购单，支持采购跟进、供应商通知、退货、导出和后续入库/财务边界。

## 组件快照

- 入口：`COMPONENTS/purchase-supplier/README.md`
- 来源：`COMPONENTS/purchase-supplier/SOURCE_MAP.md`
- API 契约：`COMPONENTS/purchase-supplier/docs/contracts/API_CONTRACT.md`
- 数据契约：`COMPONENTS/purchase-supplier/docs/contracts/DATA_CONTRACT.md`
- 权限契约：`COMPONENTS/purchase-supplier/docs/contracts/PERMISSION_CONTRACT.md`
- 验收证据：`COMPONENTS/purchase-supplier/docs/acceptance/REVIEW_EVIDENCE.md`

## 来源分层

| 层级 | 来源 | 结论 |
| :--- | :--- | :--- |
| 简版供应商 | `zhongsheng-AI` 的 `SupplierController`、`Supplier`、`supplier` SQL | 只覆盖供应商 CRUD |
| 复杂供应商 | qmy-java `PurYtSupplierController`、供应商实体/Mapper | 覆盖供应商、标签、联系人、跟进、规格对照、采购趋势 |
| 待采购 | qmy-java `PurYtApplyPurchaseController`、qmy-admin `pending` 页面/API | 覆盖待采购、换供应商、追加采购、撤回、生成采购单 |
| 采购单 | qmy-java `PurYtPurchaseController`、qmy-admin `purchased` 页面/API | 覆盖采购单、明细、退货、跟进、通知、导出 |
| 库存预警 | qmy-java `PurYtStoreWarningController`、qmy-admin `stock-warning` 页面/API | 覆盖库存预警申购 |
| 调研/PRD | `RAW/docs/zhongsheng`、`PRD_Detailed_V2.md` | 支撑报价阶段不强绑定供应商、采购阶段聚合采购单的业务边界 |

## 能力清单

| 能力 | 后端证据 | 前端证据 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础供应商 CRUD | 有 | 无专用 legacy 前端 | `zhongsheng-AI` 简版供应商 |
| 供应商列表/详情 | 有 | 有 | qmy-java/qmy-admin YT 供应商 |
| 供应商联系人/标签/跟进 | 有 | 有 | 多个接口权限注解为空或注释 |
| 供应商规格对照 | 有 | 有 | 依赖产品规格和供应商价格 |
| 待采购申请 | 有 | 有 | 订单或库存预警进入采购前的中间态 |
| 生成/追加采购单 | 有 | 有 | 必须防重复生成 |
| 已采购列表/详情 | 有 | 有 | 采购单主档和明细 |
| 采购退货 | 有 | 有 | 需联动订单、仓储、财务 |
| 库存预警申购 | 有 | 有 | 仓储边界 |
| 采购导出/通知供应商 | 有 | 有 | 需权限和数据范围控制 |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `supplier` | `code`、`name`、`contact`、`phone`、`email`、`address`、`status` | legacy 供应商主档 |
| `pur_yt_supplier` | `code`、`name`、`short_name`、`address` | YT 供应商主档 |
| `pur_yt_apply_purchase` | `product_id`、`supplier_id`、`customer_id`、`number`、`order_sub_item_id` | 待采购申请 |
| `pur_yt_purchase` | `code`、`status`、`supplier_id`、`delivery_time`、`pay_method`、`total_amount` | 采购单主表 |
| `pur_yt_purchase_item` | `purchase_id`、`apply_purchase_id`、`product_id`、`specification_id`、`number`、`enter_number` | 采购单明细 |
| `pur_yt_store_warning` | `product_id`、`specification_id`、库存/在途数量、`apply_purchase_number` | 库存预警 |
| `pro_yt_product_specification_supplier` | 产品规格、供应商规格、供应商价格 | 供应商规格对照 |

## 权限边界

- 采购路由已包含库存预警、待采购、已采购、供应商四类菜单权限。
- 已确认供应商新增/列表/更新/详情、待采购核心动作、采购单核心动作、库存预警核心动作有部分后端权限码。
- 已确认供应商跟进/标签/联系人、采购单产品/半成品、退货记录、通知供应商、撤回申购、删除暂存采购单等存在权限为空或注释。
- 已确认 `follow/delete`、`contact/delete` 等来源为 GET 破坏性接口，新项目应收口为 DELETE/POST。
- 数据范围至少需要覆盖采购员、部门负责人、销售只读、仓储、财务、老板视角。

## 接入步骤

1. 判断目标项目只要供应商 CRUD，还是要完整采购申请/采购单链路。
2. 先接认证权限、产品物料和订单组件。
3. 设计供应商、采购申请、采购单、采购明细、库存预警、付款和跟进表。
4. 接入供应商档案、联系人、标签、跟进和规格对照。
5. 接入待采购、换供应商、生成采购单、追加、撤回。
6. 接入采购单列表、详情、退货、跟进、通知和导出。
7. 再接仓储入库、库存流水、财务付款和对账。

## 验收清单

- [x] 供应商 CRUD 可追溯。
- [x] 采购申请和采购单后端参考证据已追溯到 qmy-java。
- [x] 采购申请和采购单接入缺口已明确列出。
- [x] 报价阶段不强绑定供应商、采购阶段按供应商聚合采购单的规则已写入。
- [x] `component.yaml` 保持 `status: draft`。
- [ ] 正式 DDL、权限闭环、状态机和运行验收完成前不得标记可直接复用。

## 已知风险

- `zhongsheng-AI` 只适合供应商基础档案，不覆盖采购单。
- qmy-java 复杂采购没有随源码提供完整 DDL。
- qmy-java 多个采购子资源接口存在权限缺口或注释权限。
- 采购退货、入库、付款和对账横跨订单、仓储、财务，新项目不能只交付采购页面就判定闭环。
