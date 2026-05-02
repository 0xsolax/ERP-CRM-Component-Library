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
| `RAW/PROJECTs/qmy-java` | Java 历史项目 | 客户管理 SOL-47、报价管理 SOL-48 已局部深挖，其他模块待后续 ingest | 局部 ingest |
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

### 调研资料

- `RAW/docs/zhongsheng/第一次调研（2026.02.11）/第一次会议.md`
- `RAW/docs/zhongsheng/第一次调研（2026.02.11）/第二次会议.md`
- `RAW/docs/zhongsheng/第二次调研/会议记录.md`
- `RAW/docs/zhongsheng/第二次调研/需求点：.md`

## Manifest 策略

- `wiki/source_manifest.tsv` 记录当前 `RAW/` 下非 `.DS_Store`、非嵌套 `.git/` 内部文件的 `path`、`sha256`、`size_bytes`、`mtime`。
- `.DS_Store` 是操作系统文件，不作为知识来源。
- 复制进 `RAW/PROJECTs/` 的历史项目可能包含嵌套 Git 元数据；这些对象只表达 Git 仓库状态，不表达业务或接口知识，本轮不纳入 manifest。
