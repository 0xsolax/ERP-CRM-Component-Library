# 来源索引

## 范围

本次首轮整理覆盖以下来源：

| 来源 | 类型 | 当前用途 | 处理状态 |
| :--- | :--- | :--- | :--- |
| `RAW/PROJECTs/qmy-admin` | Vue 3 / Vite / Element Plus 管理后台 | 前端页面、API 封装、路由和多租户模块结构 | 已 ingest 到项目页与组件卡 |
| `RAW/PROJECTs/project-scaffold` | Spring Boot 多模块脚手架 | 登录认证、JWT、租户配置、OSS、基础数据、分层规范 | 已 ingest 到项目页与组件卡 |
| `RAW/PROJECTs/zhongsheng-AI` | 中盛 ERP 旧实现与 PRD | 客户、报价、订单、产品、物料、供应商、财务、上传、调研需求 | 已 ingest 到项目页与组件卡 |
| `RAW/PROJECTs/zhongsheng-backend` | 中盛新版 Spring Boot 多模块后端 | 协议契约、权限、菜单、基础数据、产品、物料、OSS | 已 ingest 到项目页与组件卡 |
| `RAW/docs/zhongsheng` | 调研会议记录和需求点 | 业务意图、岗位职责、流程痛点、报价与客户管理需求 | 已 ingest 到项目页与组件卡 |
| `RAW/PROJECTs/qmy-java` | Java 历史项目 | 客户管理 SOL-47、报价管理 SOL-48、订单管理 SOL-49、采购供应商 SOL-50、仓储发货 SOL-51 已局部深挖，其他模块待后续 ingest | 局部 ingest |
| `RAW/PROJECTs/qmy-zhongsheng-ai` | 中圣专用 ERP/CRM 单仓 | 中圣业务迁移后的当前实现、GUIDE、验收报告、设计草案、Browser 证据和脚本证据 | 已建立项目页和近期进展综合页；已补抽公共单据动作、生产单管理、供应商询价台账三个组件 |
| `RAW/PROJECTs/jewelry-design` | 前端项目 | 暂未深挖 | 待后续 ingest |

## 关键来源路径

### 前端管理后台

- `RAW/PROJECTs/qmy-admin/README.md`
- `RAW/PROJECTs/qmy-admin/src/views/admin`
- `RAW/PROJECTs/qmy-admin/src/views/sed`
- `RAW/PROJECTs/qmy-admin/src/views/zs`
- `RAW/PROJECTs/qmy-admin/src/api/admin`
- `RAW/PROJECTs/qmy-admin/src/api/sed`
- `RAW/PROJECTs/qmy-admin/src/api/zs`
- `RAW/PROJECTs/qmy-admin/src/components/bz-table`
- `RAW/PROJECTs/qmy-admin/src/components/search-form`
- `RAW/PROJECTs/qmy-admin/src/components/bz-upload`

### 后端基座

- `RAW/PROJECTs/project-scaffold/README.md`
- `RAW/PROJECTs/project-scaffold/project-api`
- `RAW/PROJECTs/project-scaffold/project-core`
- `RAW/PROJECTs/project-scaffold/docs/sql/init-auth.sql`
- `RAW/PROJECTs/project-scaffold/docs/sql/init-base-data.sql`
- `RAW/PROJECTs/project-scaffold/docs/sql/init-system-file.sql`
- `RAW/PROJECTs/project-scaffold/docs/sql/init-tenant.sql`

#### 基础数据组件关键路径

- `RAW/PROJECTs/project-scaffold/project-api/src/main/java/com/qmy/project/api/dto/base`
- `RAW/PROJECTs/project-scaffold/project-application/src/main/java/com/qmy/project/config/BaseTreeNodeDataInitializer.java`
- `RAW/PROJECTs/project-scaffold/project-common/src/main/java/com/qmy/project/common/enums/BaseTreeBizTypeEnum.java`
- `RAW/PROJECTs/project-scaffold/project-common/src/main/java/com/qmy/project/common/enums/BaseTreeNodeSeedEnum.java`
- `RAW/PROJECTs/project-scaffold/project-core/src/main/java/com/qmy/project/core/base`

### 中盛业务实现

- `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller`
- `RAW/PROJECTs/zhongsheng-backend/README.md`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql`

#### 中盛基础数据关键路径

- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/base`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-application/src/main/java/com/qmy/zhongsheng/config/BaseTreeNodeDataInitializer.java`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/enums/BaseTreeBizTypeEnum.java`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/enums/BaseTreeNodeSeedEnum.java`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/constants/ApiPermissionConstants.java`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/base`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-base-data.sql`

#### 产品物料组件关键路径

- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/product`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/material`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/process`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/process`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-product.sql`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-material.sql`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-fabric.sql`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-packaging.sql`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-process.sql`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-umbrella-frame.sql`
- `RAW/PROJECTs/qmy-admin/src/views/zs/base-info/product`
- `RAW/PROJECTs/qmy-admin/src/views/zs/base-info/process`
- `RAW/PROJECTs/qmy-admin/src/views/zs/material`
- `RAW/PROJECTs/qmy-admin/src/api/zs/product`
- `RAW/PROJECTs/qmy-admin/src/api/zs/material`
- `RAW/PROJECTs/qmy-admin/src/api/zs/base-info/process.ts`

#### 客户管理组件关键路径

- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/CustomerController.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Customer.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`
- `RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer`
- `RAW/PROJECTs/qmy-admin/src/views/sed/sales/customer`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/sales.ts`
- `RAW/PROJECTs/qmy-admin/src/views/sed/router/async-modules/sales.ts`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtCustomerController.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/yt/SalYtCustomerManager.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt/SalYtCustomerMapper.xml`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt/SalYtCustomerAddressMapper.xml`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt/SalYtContactPersonMapper.xml`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt/SalYtCustomerFollowMapper.xml`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/annotation/RequiresDataPermissions.java`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/interceptor/AuthenticationInterceptor.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/interceptor/TenantInterceptor.java`

#### 报价管理组件关键路径

- `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Quote.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/sed/SalSedQuotationController.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/service/sal/sed/SalSedQuotationService.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/sed/SalSedQuotationManager.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/sed/SalSedQuotationSkuManager.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/sed/SalSedQuotationSkuPackingManager.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sal/sed/SalSedQuotationMapper.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/sed/SalSedQuotationMapper.xml`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/sed/SalSedQuotationSkuMapper.xml`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/sed/SalSedQuotation.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/sed/SalSedQuotationSku.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/sed/SalSedQuotationSkuPacking.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/sed/SalSedQuotationHistory.java`
- `RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation`
- `RAW/PROJECTs/qmy-admin/src/api/sed/sales/quotation.ts`
- `RAW/PROJECTs/qmy-admin/src/api/sed/product/packing.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/sed/quotation.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/sed/sales.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/file-type.ts`
- `RAW/PROJECTs/qmy-admin/src/interface/table.ts`
- `RAW/PROJECTs/qmy-admin/src/views/sed/router/async-modules/sales.ts`

#### 订单管理组件关键路径

- `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/OrdersController.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Orders.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/mapper/OrdersMapper.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/OrdersService.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/impl/OrdersServiceImpl.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtOrderController.java`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/service/sal/yt/SalYtOrderService.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/yt/SalYtOrderManager.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/yt/SalYtOrderSubItemOperationManager.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/pur/yt/PurYtApplyPurchaseManager.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pur/yt/PurYtApplyPurchase.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/param/sal/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/vo/sal/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/vo/pur/yt/PurYtApplyPurchaseListVo.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sal/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/pur/yt/PurYtApplyPurchaseMapper.java`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/pur/yt/PurYtApplyPurchaseMapper.xml`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/OrderSubItemStatusEnum.java`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/ShippingMethodEnum.java`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/ReturnOrderTypeEnum.java`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/DeliveryOrderStatusEnum.java`
- `RAW/PROJECTs/qmy-admin/src/views/admin/sales/order`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/product/index.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/auth/org.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/sales.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/finance.ts`
- `RAW/PROJECTs/qmy-admin/src/interface/table.ts`
- `RAW/PROJECTs/qmy-admin/src/components/footer-actions/index.vue`
- `RAW/PROJECTs/qmy-admin/src/components/product-selector`
- `RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/tags/index.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/sales.ts`

#### 采购供应商组件关键路径

- `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/SupplierController.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Supplier.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/mapper/SupplierMapper.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/SupplierService.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/impl/SupplierServiceImpl.java`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtSupplierController.java`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtPurchaseController.java`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtStoreWarningController.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/pur/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pur/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/pro/yt/ProYtProductSpecificationSupplier.java`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/param/pur/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/vo/pur/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/pur/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/pur/yt`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/PurchaseStatusEnum.java`
- `RAW/PROJECTs/qmy-java/web/src/main/resources/excel-template/yitang-purchaseExport.xlsx`
- `RAW/PROJECTs/qmy-admin/src/views/admin/purchase`
- `RAW/PROJECTs/qmy-admin/src/api/admin/purchase`
- `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/purchase.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/purchase.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/sales.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/file-type.ts`
- `RAW/PROJECTs/qmy-admin/src/interface/table.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/product/index.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/auth/org.ts`
- `RAW/PROJECTs/qmy-admin/src/components/footer-actions/index.vue`
- `RAW/PROJECTs/qmy-admin/src/components/product-selector`
- `RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/tags/index.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/permission/index.ts`
- `RAW/PROJECTs/qmy-admin/src/layout/admin/index.vue`
- `RAW/PROJECTs/qmy-admin/src/utils/axios`
- `RAW/PROJECTs/qmy-admin/src/utils/download.ts`
- `RAW/PROJECTs/qmy-admin/src/utils/auth.ts`
- `RAW/PROJECTs/qmy-admin/src/utils/validate.ts`
- `RAW/PROJECTs/qmy-admin/src/utils/index.ts`

#### 仓储发货组件关键路径

- `RAW/PROJECTs/qmy-admin/src/views/admin/warehouse`
- `RAW/PROJECTs/qmy-admin/src/views/admin/delivery`
- `RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/delivery/index.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/warehouse.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/delivery.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/warehouse.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/delivery.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/sales.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/yitang/product.ts`
- `RAW/PROJECTs/qmy-admin/src/constant/file-type.ts`
- `RAW/PROJECTs/qmy-admin/src/interface/table.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/product/index.ts`
- `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`
- `RAW/PROJECTs/qmy-admin/src/components/footer-actions/index.vue`
- `RAW/PROJECTs/qmy-admin/src/components/product-selector`
- `RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/tags/index.ts`
- `RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/permission/index.ts`
- `RAW/PROJECTs/qmy-admin/src/layout/admin/index.vue`
- `RAW/PROJECTs/qmy-admin/src/hooks/handle/use-handle.ts`
- `RAW/PROJECTs/qmy-admin/src/utils/axios`
- `RAW/PROJECTs/qmy-admin/src/utils/validate.ts`
- `RAW/PROJECTs/qmy-admin/src/utils/print.ts`
- `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sto/yt`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sto/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sto/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/param/sto/yt`
- `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/vo/sto/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sto/yt`
- `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sto/yt`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/event/yt/DeliveryEvent.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/event/yt/StoreChangeEvent.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/listener/yt/DeliveryEventListener.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/listener/yt/StoreEventListener.java`
- `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/job/YtStoreWarningJob.java`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/DeliveryOrderStatusEnum.java`
- `RAW/PROJECTs/qmy-java/core/src/main/java/com/qiaomoyun/eunm/yt/StoreEnterOutTypeEnum.java`
- `RAW/PROJECTs/qmy-java/web/src/main/resources/excel-template/yitang-deliveryExport.xlsx`
- `RAW/PROJECTs/qmy-java/web/src/main/resources/excel-template/ytiang-customerDeliveryCostImport.xlsx`

#### SOL-52 装配演练关键路径

- `COMPONENTS/customer-management`
- `COMPONENTS/quote-management`
- `COMPONENTS/order-management`
- `COMPONENTS/file-oss`
- `COMPONENTS/customer-management/README.md`
- `COMPONENTS/quote-management/README.md`
- `COMPONENTS/order-management/README.md`
- `COMPONENTS/file-oss/README.md`
- `COMPONENTS/customer-management/docs/contracts`
- `COMPONENTS/quote-management/docs/contracts`
- `COMPONENTS/order-management/docs/contracts`
- `COMPONENTS/file-oss/docs/contracts`
- `COMPONENTS/customer-management/docs/acceptance/ACCEPTANCE.md`
- `COMPONENTS/quote-management/docs/acceptance/ACCEPTANCE.md`
- `COMPONENTS/order-management/docs/acceptance/ACCEPTANCE.md`
- `COMPONENTS/file-oss/docs/acceptance/ACCEPTANCE.md`
- `wiki/SYNTH_SOL52客户报价订单文件装配演练.md`
- `pdoc/report/REPORT_0502_SOL52装配演练报告.md`
- `pdoc/report/REPORT_0502_SOL52装配缺口清单.md`

### 中圣专用单仓

- `RAW/PROJECTs/qmy-zhongsheng-ai/README.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/AGENTS.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_项目接手引导.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_文档索引.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL53父级完成情况复核.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL63总体验收与回归.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_观察页面.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/design/DESIGN_0509_财务模块.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0504_SOL69生产总单派生采购与快捷新增.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0505_中圣入口收口与一唐隐藏入口.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0505_系统管理管理员化.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0506_供应商询价台账开发.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0506_观察页面与工具箱隐藏.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0507_主链统一编号复刻.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_产品表单快捷新增补齐.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_生产单旧宽表残留清理.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_生产总单详情抽屉产品行交货合并.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/material/FLOW_REGRESSION_20260506_B01/`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql`

#### qmy-zhongsheng-ai 新组件关键路径

公共单据动作与审计日志：

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_公共单据动作.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/document`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/system`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-document-action.sql`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/components/document-action-log-drawer`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/system/operation-log`
- `COMPONENTS/document-action`
- `wiki/COMP_公共单据动作与审计日志.md`

生产单管理：

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_生产单管理.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/production`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/production`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-production.sql`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/resources/excel/production-order-template.xls`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/production`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs/production`
- `COMPONENTS/production-management`
- `wiki/COMP_生产单管理.md`

供应商询价台账：

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/supplier`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/supplier`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-supplier-inquiry.sql`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/supplier-inquiry`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs/supplier-inquiry`
- `COMPONENTS/supplier-inquiry`
- `wiki/COMP_供应商询价台账.md`

### 调研资料

- `RAW/docs/zhongsheng/第一次调研（2026.02.11）/第一次会议.md`
- `RAW/docs/zhongsheng/第一次调研（2026.02.11）/第二次会议.md`
- `RAW/docs/zhongsheng/第二次调研/会议记录.md`
- `RAW/docs/zhongsheng/第二次调研/需求点：.md`

## Manifest 策略

- `wiki/source_manifest.tsv` 记录当前 `RAW/` 下可作为知识来源的文件 `path`、`sha256`、`size_bytes`、`mtime`，当前覆盖 3644 个来源文件。
- `.DS_Store` 是操作系统文件，不作为知识来源。
- 复制进 `RAW/PROJECTs/` 的历史项目可能包含嵌套 Git 元数据；这些对象只表达 Git 仓库状态，不表达业务或接口知识，本轮不纳入 manifest。
- `node_modules/`、Maven `target/`、`.next/`、`coverage/`、`.local-run/` 是依赖、构建或运行产物，不作为知识来源；`qmy-admin/build/` 是源码内构建脚本目录，继续保留在 manifest。
