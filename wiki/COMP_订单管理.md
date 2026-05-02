# COMP 订单管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 销售履约 |
| 快照路径 | `COMPONENTS/order-management` |
| 复用等级 | `reference`，可参考改造 |
| 适用项目 | 报价转订单、手工订单、采购申请、退货、发货、完结、关闭等销售履约场景 |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 业务目标

将客户报价或销售确认转成可履约订单，跟踪客户、来源报价、订单明细、状态、采购申请、退货、发货、完结、关闭和导出，同时明确采购、仓储、财务只是跨组件边界。

## 组件快照

- 入口：`COMPONENTS/order-management/README.md`
- 来源：`COMPONENTS/order-management/SOURCE_MAP.md`
- API 契约：`COMPONENTS/order-management/docs/contracts/API_CONTRACT.md`
- 数据契约：`COMPONENTS/order-management/docs/contracts/DATA_CONTRACT.md`
- 权限契约：`COMPONENTS/order-management/docs/contracts/PERMISSION_CONTRACT.md`
- 验收证据：`COMPONENTS/order-management/docs/acceptance/REVIEW_EVIDENCE.md`

## 来源分层

| 层级 | 来源 | 结论 |
| :--- | :--- | :--- |
| 简版订单 | `zhongsheng-AI` 的 `OrdersController`、`Orders`、`orders/order_item` | 只覆盖基础 CRUD、客户、来源报价、交期、金额和简单状态 |
| 复杂订单 | qmy-java 的 `SalYtOrderController`、`SalYtOrderManager`、订单实体与 Mapper | 覆盖主订单、子订单、商品项、审核、退货、半成品、发货、完结、关闭、导出 |
| 采购申请 | qmy-java 的 `PurYtApplyPurchaseController`、`PurYtApplyPurchaseManager` | 订单生成采购需求的边界，不包含采购单全流程 |
| 前端 | qmy-admin `views/admin/sales/order`、`api/admin/sales/order.ts` | 覆盖订单列表、新增、编辑、详情、申请采购、退货、物流、导出页面 |
| 调研/PRD | `RAW/docs/zhongsheng`、`PRD_Detailed_V2.md` | 支撑订单、采购、退货、仓储、财务边界判断 |

## 能力清单

| 能力 | 后端证据 | 前端证据 | 说明 |
| :--- | :--- | :--- | :--- |
| 基础订单 CRUD | 有 | 无专用 legacy 前端 | `zhongsheng-AI` 简版订单 |
| 复杂订单列表/详情 | 有 | 有 | qmy-java/qmy-admin YT 订单 |
| 新增/编辑订单 | 有 | 有 | 涉及客户、产品规格、地址、业务员、跟单员 |
| 审核/汇率 | 有 | 有 | 金额敏感操作 |
| 来源报价 | 有字段证据 | 有字段/业务入口证据 | 直接转订单能力由 `quote-management` 承接 |
| 申请采购 | 有边界后端 | 有 | 仅生成采购申请，后续归采购组件 |
| 退货 | 有 | 有 | 订单退货和商品项退货 |
| 半成品确认 | 有 | 有 | 订单商品项状态流 |
| 发货/物流 | 有边界后端 | 有 | 完整仓储/物流归仓储组件 |
| 完结/关闭 | 有 | 有 | 状态变更，需审计 |
| 导出/物流导出 | 有 | 有 | 必须强制数据范围 |
| 财务回款/利润 | 有字段/展示证据 | 有 | 真实财务流水归财务组件 |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `orders` | `code`、`customer_id`、`quote_id`、`delivery_date`、`total_amount`、`status` | legacy 订单主表 |
| `order_item` | `order_id`、`product_id`、`quantity`、`unit_price`、`amount` | legacy 订单明细 |
| `sal_yt_order` | `code`、`customer_id`、`source_platform`、`currency`、`status`、`shipping_method`、`receive_status` | YT 订单主表 |
| `sal_yt_order_sub` | `sub_code`、`order_id`、`order_type`、`receive_status` | 子订单 |
| `sal_yt_order_sub_item` | `product_id`、`specification_id`、`price`、`number`、`status`、`supplier_id`、`enter_number`、`delivery_number` | 商品项 |
| `sal_yt_return_order` | `type`、`order_sub_item_id`、`return_number`、`reason` | 退货记录 |
| `pur_yt_apply_purchase` | `product_id`、`supplier_id`、`customer_id`、`number`、`order_sub_item_id` | 采购申请桥接 |

## 权限边界

- 已确认 qmy-java `POST /sal/yt/order/list` 有 `@RequiresDataPermissions`。
- 已确认详情、子订单详情、退货统计、半成品、关闭、物流详情等多个接口权限为空或注释，接入前必须补齐。
- 已确认 `/sal/yt/order/delete`、`/sal/yt/order/confirmComplete` 来源为 GET 状态变更接口，新项目应收口为 DELETE/POST。
- 数据范围至少需要覆盖本人、部门、全公司、老板视角，并按客户归属、业务员、跟单员或组织树限制。
- 导出、物流导出、退货、关闭和采购申请必须在后端重新校验订单范围，不能依赖列表页。

## 接入步骤

1. 判断目标项目采用简版 `orders/order_item`，还是复杂 `sal_yt_order*` 模型。
2. 先接入认证权限、客户、产品物料、报价组件。
3. 设计订单 DDL、状态机、编号规则、金额精度、跨模块引用。
4. 接入订单列表、详情、新增编辑、审核和明细。
5. 按需接入采购申请、退货、半成品确认、发货、完结、关闭和导出。
6. 补后端权限、数据范围、状态前置校验和操作记录。
7. 再接采购、仓储、财务组件的真实后续动作。

## 验收清单

- [x] 订单能关联客户。
- [x] 订单能关联来源报价单字段。
- [x] 订单主从表、状态和明细已明确。
- [x] 采购申请边界已明确。
- [x] 退货、完结、关闭、仓储/财务边界已明确。
- [x] 有后端证据和仅前端/边界证据已区分。
- [ ] 复杂订单 DDL、数据范围守卫和 HTTP 方法收口需目标项目接入前补齐。

## 已知风险

- `zhongsheng-AI` 只适合简版订单，不等于完整履约链路。
- qmy-java 没有随源码提供完整订单 DDL。
- qmy-java 多个敏感接口存在权限缺口或注释权限，不能直接用于生产。
- 订单连接采购、仓储、财务，新项目不能只交付订单列表页就判定履约闭环。
