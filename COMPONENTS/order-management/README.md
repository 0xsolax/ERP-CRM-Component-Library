# order-management 订单管理组件

## 定位

`order-management` 提供 ERP/CRM 销售链路中的订单主档、子订单、商品项、订单状态、审核、申请采购、退货、半成品确认、发货确认、完结、关闭和导出能力。

本组件是可追溯快照，不是已打包 SDK。新项目接入前必须先确认订单复杂度：只需要基础订单 CRUD 时参考 `zhongsheng-AI`；需要订单拆分、采购申请、仓储发货、退货和财务回款边界时参考 qmy-java 与 qmy-admin 的 YT 复杂流。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 业务组件 |
| 必选依赖 | `BASE/project-scaffold`、`auth-permission`、`customer-management`、`product-material`、`quote-management` |
| 可选依赖 | `purchase-supplier`、`warehouse-delivery`、`finance`、`file-oss` |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-AI/` | legacy 订单 CRUD：`OrdersController`、`Orders`、`OrdersService`、`OrdersMapper` |
| `backend/qmy-java/` | YT 复杂订单：订单 controller/service/manager、采购申请 controller/manager、entity、param、VO、Mapper、枚举 |
| `frontend/qmy-admin/` | admin 订单列表、新增、编辑、详情、申请采购、退货、发货、导出页面和 API |
| `db/` | legacy `orders/order_item` SQL，以及 qmy-java 订单表结构说明 |
| `docs/source/` | PRD 和调研材料来源副本 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照和接入验收清单、复核证据 |

## 能力边界

已覆盖：

- 基础订单 CRUD：分页、详情、新增、编辑、删除、客户、来源报价、交期、金额、状态。
- 复杂订单主档：客户、平台、币种、销售/跟进、收货地址、发货形式、入库发货、运费、汇率、回款状态。
- 子订单和商品项：成品单、半成品单、产品规格、数量、价格、供应商、库存占用、在途占用、采购数量、入库数量、发货数量。
- 状态流：暂存、待采购、待入库、待打包、待发货、已发货、已完成、待确认、已关闭。
- 审核和汇率：订单审核、汇率设置。
- 采购申请：订单商品项生成 `pur_yt_apply_purchase`，采购单后续流转归采购组件。
- 退货：订单退货、订单商品项退货记录、退货统计和按规格退货记录。
- 发货与完结：确认发货、物流信息、包裹详情、修改发货方式、确认完成。
- 关闭：关闭预览、关闭订单、关闭金额和关闭原因。
- 导出：订单导出和物流导出。

待项目确认：

- `zhongsheng-AI` 的 `orders/order_item` 是简版 CRUD；没有审核、采购、退货、仓储、财务链路。
- qmy-java 未提供完整建表 SQL，本快照依据实体、Mapper XML 和前端字段整理数据契约。
- qmy-java 来源只有列表接口标注 `@RequiresDataPermissions`，详情、删除、退货、发货、完结、关闭和导出需目标项目补订单归属/客户范围守卫。
- qmy-admin 订单页面依赖客户、产品、采购、仓储、财务、组织用户、下载、上传和全局布局，本组件只复制订单直接入口和必要 API/常量。
- 采购申请、采购单、仓储入库、打包发货、财务回款不是订单组件私有能力，应由对应组件承接。

## 快速接入

1. 先接入 `BASE/project-scaffold`、`auth-permission`、`customer-management`、`product-material` 和 `quote-management`。
2. 判断业务复杂度：基础订单只需 `orders/order_item`；复杂订单需 `sal_yt_order`、子订单、商品项、退货、采购申请和操作记录。
3. 建立订单主表、子订单、商品项、半成品确认、操作记录、退货记录和采购申请桥接表。
4. 接入后端订单列表、详情、新增编辑、审核、采购申请、退货、发货、完结、关闭、导出接口。
5. 接入前端 admin 订单页面、API、状态常量和裁剪后的销售管理路由入口。
6. 配置权限码：列表、详情、新增编辑、审核、删除、导出、退货、确认发货、确认完成、修改发货方式、物流导出。
7. 对详情、退货、发货、关闭、导出和采购申请补充订单归属、客户范围、租户范围和状态校验。
8. 按 `docs/acceptance/ACCEPTANCE.md` 做快照验收和后续装配验收。

## 安全与业务规则

- 订单包含客户、收货信息、价格、成本、采购、库存、发货和回款状态，必须按后端权限和数据范围控制。
- 订单删除、退货、确认发货、确认完成、关闭订单都是状态变更，必须具备状态前置校验和操作记录。
- 申请采购只能生成采购需求，不应在订单组件内完成采购单付款、入库和供应商对账。
- 发货和包裹详情属于仓储/物流边界，订单组件只发起状态确认和查看结果。
- 回款和利润状态属于财务边界，订单组件只展示状态，不应直接维护财务流水。
- 前端隐藏按钮不能替代接口权限，尤其是详情、退货、关闭、导出等敏感接口。
