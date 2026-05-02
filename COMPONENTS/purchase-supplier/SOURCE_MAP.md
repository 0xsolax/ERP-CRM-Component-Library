# SOURCE_MAP｜purchase-supplier

## 来源摘要

| 类型 | 路径 | 用途 | 处理方式 |
| :--- | :--- | :--- | :--- |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/SupplierController.java` | 简版供应商 CRUD | 复制到 `backend/zhongsheng-AI/` |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Supplier.java` | 简版供应商主档 | 复制到 `backend/zhongsheng-AI/` |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service`、`mapper/SupplierMapper.java` | 简版供应商分层 | 复制到 `backend/zhongsheng-AI/` |
| legacy SQL | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | `supplier` 表 | 提取到 `db/zhongsheng-AI/init-supplier.sql` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt` | 供应商、待采购、采购单、库存预警 API | 复制到 `backend/qmy-java/` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/pur/yt` | 采购业务逻辑 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pur/yt` | 供应商、采购申请、采购单、采购明细、库存预警 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pro/yt/ProYtProductSpecificationSupplier.java` | 供应商规格对照 | 复制到 `backend/qmy-java/` |
| 复杂流数据 | `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/pur/yt` | 采购查询、聚合和持久化证据 | 复制到 `backend/qmy-java/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/purchase` | 供应商、待采购、已采购、库存预警页面 | 复制到 `frontend/qmy-admin/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/api/admin/purchase` | 采购 API 封装 | 复制到 `frontend/qmy-admin/` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/purchase.ts` | 采购路由和权限码 | 复制到 `frontend/qmy-admin/` |
| 调研/PRD | `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`、`RAW/docs/zhongsheng` | 采购业务规则和岗位边界 | 复制到 `docs/source/` |

## 抽取范围

已抽取：

- legacy `supplier` CRUD 后端和 SQL。
- qmy-java 供应商、待采购、采购单、库存预警 controller/manager/entity/param/VO/mapper/enum。
- qmy-admin 供应商、待采购、已采购、库存预警页面、API、路由和采购常量。
- 采购导出模板 `yitang-purchaseExport.xlsx`。
- PRD 与调研材料。

未抽取：

- 仓库入库、库存变更、在途、打包发货完整实现；归 `warehouse-delivery`。
- 财务付款、应付、对账、利润完整实现；归 `finance`。
- 产品、规格、供应商规格维护的产品主数据部分；归 `product-material`。
- 客户、订单、业务员主数据维护；归 `customer-management`、`order-management` 和认证组织基座。
- qmy-admin 全局布局、请求封装、权限守卫、下载工具、上传组件、通用表格等通用能力。

待验证：

- qmy-java 采购完整 DDL、索引、唯一键、软删除和租户字段。
- 采购申请到采购单的状态机、重复生成防护、撤回规则和追加规则。
- 供应商子资源写接口和采购单敏感查询的后端权限/数据范围闭环。
- 采购导出、通知供应商、退货、付款和入库在目标项目中的跨组件责任划分。

## 清洗规则

- 未复制源项目嵌套 `.git/`、`.DS_Store`、构建产物、依赖目录和环境配置。
- 采购页面直接依赖的产品、客户、组织用户、常量、表格类型文件已作为依赖证据带出。
- 仓储和财务只在契约中写边界，不复制完整实现。
- 原始 `RAW/` 只读，不在抽取过程中修改。

## 事实与推断

### 已确认事实

- `zhongsheng-AI` 的 `SupplierController` 只提供 `/api/supplier` 基础 CRUD。
- `zhongsheng-AI` 的 `init.sql` 有 `supplier` 表，没有采购申请和采购单表。
- qmy-admin 采购 API 使用 `/pur/yt/supplier/*`、`/pur/yt/applyPurchase/*`、`/pur/yt/purchase/*`、`/pur/yt/storeWarning/*`。
- qmy-java 存在对应的 `PurYtSupplierController`、`PurYtApplyPurchaseController`、`PurYtPurchaseController`、`PurYtStoreWarningController` 和 manager/mapper/entity 证据。
- qmy-java 多个供应商子资源、采购单子查询、退货/跟进/通知接口权限为空或注释。

### 推断

- 供应商基础档案可以独立接入，但完整采购流必须依赖订单、产品、库存和财务。
- 待采购列表是订单/库存预警生成采购申请后的中间态，不等同于采购单。
- 采购单应按供应商聚合，并保留订单商品项或库存预警来源，避免采购后无法追溯。

### 待验证问题

- 报价阶段是否完全不展示供应商，还是只隐藏默认供应商/成本来源。
- 同一供应商采购单是否允许合并多个客户/订单/产品。
- 采购单退货后如何回写订单、库存和财务。
- 库存预警申购是否自动生成采购申请，还是必须人工确认。
