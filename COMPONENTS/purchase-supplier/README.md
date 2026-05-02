# purchase-supplier 采购供应商组件草稿

## 定位

`purchase-supplier` 用于沉淀供应商档案、供应商联系人/标签/跟进、待采购申请、采购单、采购退货、库存预警申购和供应商规格对照等采购链路证据。

本组件按 SOL-50 要求标记为 `draft`。它可以作为采购业务分析和后续实现参考，但未完成目标项目级 DDL、权限、状态机和运行验收前，不得标记为可直接复用。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `draft` |
| 组件类型 | 待验证业务组件 |
| 必选依赖 | `BASE/project-scaffold`、`auth-permission`、`product-material`、`order-management` |
| 可选依赖 | `customer-management`、`warehouse-delivery`、`finance`、`file-oss` |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-AI/` | legacy 供应商 CRUD：`SupplierController`、`Supplier`、`SupplierService`、`SupplierMapper` |
| `backend/qmy-java/` | YT 供应商、待采购、采购单、库存预警、采购跟进、采购退货、采购导出后端证据 |
| `frontend/qmy-admin/` | admin 采购页面、采购 API、采购路由、采购状态常量和直接依赖 API |
| `db/` | legacy `supplier` SQL，以及 qmy-java 采购表结构说明 |
| `docs/source/` | PRD 和调研材料来源副本 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照验收和复核证据 |

## 能力边界

已覆盖证据：

- 简版供应商 CRUD：分页、详情、新增、编辑、删除。
- 复杂供应商档案：供应商主档、标签、联系人、跟进记录、供应商规格对照、采购趋势/占比。
- 待采购申请：订单或库存预警形成采购申请、待采购列表、更换供应商、追加采购、撤回申购。
- 采购单：生成/编辑采购单、暂存采购单、采购单详情、产品/半成品列表、采购跟进、通知供应商、导出。
- 退货：采购单退货、退货记录、退货统计、退货详情。
- 库存预警：库存预警列表、申购详情、提交申购、测试生成预警。

必须保持边界：

- 报价阶段不强绑定供应商；采购阶段再按供应商聚合采购单。
- 产品、规格、供应商规格对照依赖 `product-material`。
- 订单商品项、客户、业务员、采购申请来源依赖 `order-management` 和 `customer-management`。
- 入库、库存、在途、打包发货由 `warehouse-delivery` 承接。
- 付款、应付、对账由 `finance` 承接。

## 快速接入

1. 先接入认证权限、产品物料和订单组件。
2. 判断目标项目只需要供应商 CRUD，还是需要完整采购申请到采购单链路。
3. 简版供应商可参考 `supplier` 表和 `/api/supplier`。
4. 复杂采购需重新设计 `pur_yt_supplier`、`pur_yt_apply_purchase`、`pur_yt_purchase`、`pur_yt_purchase_item`、库存预警、付款和跟进表。
5. 接入 qmy-admin 采购页面前，先补齐产品、客户、组织用户、下载、上传、权限守卫等基座依赖。
6. 补齐供应商/采购单/采购申请后端权限、数据范围、状态前置校验和操作记录。
7. 按 `docs/acceptance/ACCEPTANCE.md` 做快照验收和目标项目装配验收。

## 不能直接复用的原因

- `zhongsheng-AI` 只提供供应商基础 CRUD，没有采购单模型。
- qmy-java 提供复杂流参考，但没有完整 DDL 和目标项目运行验收。
- qmy-java 采购 controller 多个子资源和查询接口权限为空或注释，`follow/delete`、`contact/delete` 等仍使用 GET。
- 采购状态、入库、付款、退货、通知供应商等动作跨订单、仓储、财务多个组件，不能孤立交付。
