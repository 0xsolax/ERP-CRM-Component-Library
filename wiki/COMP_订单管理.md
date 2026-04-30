# COMP 订单管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 销售履约 |
| 复用等级 | 可参考改造 |
| 适用项目 | 报价转订单、生产履约、采购联动场景 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/order`、`RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/OrdersController.java`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`、`RAW/docs/zhongsheng` |

## 业务目标

将客户报价或销售确认转成订单，跟踪交期、明细、状态、退货、采购申请和生产/仓储后续动作。

## 前端入口

- 页面：`RAW/PROJECTs/qmy-admin/src/views/admin/sales/order/index.vue`、`add.vue`、`detail.vue`、`edit.vue`、`purchase.vue`。
- API：`RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 订单分页 | GET | `/api/orders/page` | `OrdersController` |
| 订单详情 | GET | `/api/orders/{id}` | `OrdersController` |
| 新增订单 | POST | `/api/orders` | `OrdersController` |
| 编辑订单 | PUT | `/api/orders` | `OrdersController` |
| 删除订单 | DELETE | `/api/orders/{id}` | `OrdersController` |
| 订单列表 | POST | `/sal/yt/order/list` | `qmy-admin` API |
| 订单审核 | POST | `/sal/yt/order/audit` | `qmy-admin` API |
| 申请采购 | POST | `/pur/yt/applyPurchase/saveOrUpdate` | `qmy-admin` API |
| 退货/完结 | POST/GET | `/sal/yt/order/return*`、`/confirmComplete` | `qmy-admin` API |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `orders` | `code`、`customer_id`、`quote_id`、`order_date`、`delivery_date`、`total_amount`、`status` | 订单主表 |
| `order_item` | `order_id`、`product_id`、`product_name`、`quantity`、`unit_price`、`amount` | 订单明细 |
| 采购申请 | 订单、产品/配件、数量、供应商 | 前端 API 有申请采购路径 |

## 权限边界

- 订单通常至少需要查看、新增/编辑、审核、删除/作废、申请采购、确认完成。
- 调研资料提到老板全局可见、部门隔离；订单数据范围需项目级确认。

## 接入步骤

1. 明确订单来源：手工新增、报价转订单、外部平台导入。
2. 对齐订单状态：待处理、生产中、完成、取消等是否满足目标项目。
3. 接入订单明细和产品/物料引用。
4. 决定是否启用审核、采购申请、退货、半成品确认。
5. 与仓储发货、财务应收、生产单模块对齐。

## 验收清单

- [ ] 订单能关联客户。
- [ ] 订单能关联来源报价单。
- [ ] 订单明细金额能汇总到主表。
- [ ] 订单状态流转可控。
- [ ] 申请采购或后续履约动作边界清晰。

## 已知风险

- 旧 SQL 和旧 controller 只有基础主从结构；前端 API 体现的审核、退货、半成品、采购联动需要补后端证据。
- 订单往往连接生产、采购、仓储、财务，新项目不能只交付列表页。

