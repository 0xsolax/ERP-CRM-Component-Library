# SOURCE_MAP｜quote-management

## 来源摘要

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md` | 基础报价 CRUD、API、字段和成本公式说明 |
| `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java` | `/api/quote/*` legacy 报价 CRUD |
| `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | `quote`、`quote_item`、订单 `quote_id` 来源字段 |
| `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/sed/SalSedQuotationController.java` | `/api/sal/sed/quotation/*` 复杂报价接口 |
| `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/service/sal/sed/SalSedQuotationService.java` | 报价状态、审核、成本确认和详情组装 |
| `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/sed` | 成本明细、历史报价、导出、转订单和总成本计算 |
| `RAW/PROJECTs/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sal/sed` | 报价和转订单 Mapper 接口 |
| `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/sed` | 报价列表、详情、历史报价、SKU、包材和订单明细 SQL |
| `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/param/vo/sal/sed` | 报价主表、SKU、包材、历史、参数和视图对象 |
| `RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation` | SED 报价前端页面和弹窗 |
| `RAW/PROJECTs/qmy-admin/src/api/sed/sales/quotation.ts` | SED 报价前端 API 封装 |
| `RAW/PROJECTs/qmy-admin/src/constant/sed/quotation.ts` | 报价状态和操作类型 |
| `RAW/PROJECTs/qmy-admin/src/constant/sed/sales.ts` | 币种、税、FOB、EXW、订单来源 |
| `RAW/PROJECTs/qmy-admin/src/constant/file-type.ts` | 总裁微信审核图片文件类型 |
| `RAW/docs/zhongsheng` | 客户、报价、成本和销售流程调研材料 |

## 已复制范围

### zhongsheng-AI

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `erp-backend/.../controller/QuoteController.java` | `backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java` | 基础 CRUD controller |
| `erp-backend/.../entity/Quote.java` | `backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/entity/Quote.java` | legacy 报价实体 |
| `erp-backend/.../mapper/QuoteMapper.java` | `backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/mapper/QuoteMapper.java` | MyBatis-Plus mapper |
| `erp-backend/.../service/QuoteService.java` | `backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/QuoteService.java` | service 接口 |
| `erp-backend/.../service/impl/QuoteServiceImpl.java` | `backend/zhongsheng-AI/erp-backend/src/main/java/com/erp/service/impl/QuoteServiceImpl.java` | 分页查询实现 |
| `erp-backend/src/main/resources/init.sql` | `db/zhongsheng-AI/init-quote.sql` | 抽取 `quote`、`quote_item` DDL |
| `PRD_Detailed_V2.md` | `docs/source/zhongsheng-AI/PRD_Detailed_V2.md` | 完整 PRD 来源 |

### qmy-java

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `web/.../SalSedQuotationController.java` | `backend/qmy-java/web/.../SalSedQuotationController.java` | 复杂报价接口 |
| `service/.../SalSedQuotationService.java` | `backend/qmy-java/service/.../SalSedQuotationService.java` | 业务服务 |
| `service/.../manager/sal/sed/SalSedQuotation*.java` | `backend/qmy-java/service/.../manager/sal/sed` | 成本、历史报价、体积、箱数和转订单 |
| `dao/.../mapper/sal/sed/SalSedQuotation*.java` | `backend/qmy-java/dao/src/main/java/com/qiaomoyun/mapper/sal/sed` | 报价 Mapper 接口 |
| `dao/.../mapper/sal/sed/SalSedQuotation*.xml` | `backend/qmy-java/dao/src/main/resources/mapper/sal/sed` | 报价 SQL |
| `entity/.../SalSedQuotation*.java` | `backend/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sal/sed` | 报价主表、SKU、包材、历史实体 |
| `param/.../SalSedQuotation*.java`、`SalSedHistoryQuotationInfoParams.java` | `backend/qmy-java/entity/src/main/java/com/qiaomoyun/param/sal/sed` | 报价请求参数 |
| `vo/.../SalSedQuotation*.java`、`SalSedHistoryQuotation*.java` | `backend/qmy-java/entity/src/main/java/com/qiaomoyun/vo/sal/sed` | 报价响应对象 |
| `core/.../Quotation*.java` | `backend/qmy-java/core/src/main/java/com/qiaomoyun/eunm/sed` | 报价状态、操作、会签动作 |
| `SalSedOrder*.java`、`SalSedOrder*.xml` | `backend/qmy-java/.../sal/sed` | 仅保留转订单引用证据 |
| qmy-java entity、Mapper XML | `db/qmy-java/sal-sed-quotation-schema-notes.md` | 表结构说明 |

### qmy-admin

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `src/views/sed/sales/quotation` | `frontend/qmy-admin/src/views/sed/sales/quotation` | 报价列表、弹窗、抽屉和转订单页面 |
| `src/api/sed/sales/quotation.ts` | `frontend/qmy-admin/src/api/sed/sales/quotation.ts` | 报价 API |
| `src/api/sed/product/packing.ts` | `frontend/qmy-admin/src/api/sed/product/packing.ts` | 包材选择 API |
| `src/api/admin/sales/customer.ts` | `frontend/qmy-admin/src/api/admin/sales/customer.ts` | 客户选择 API |
| `src/constant/sed/quotation.ts` | `frontend/qmy-admin/src/constant/sed/quotation.ts` | 报价状态与操作常量 |
| `src/constant/sed/sales.ts` | `frontend/qmy-admin/src/constant/sed/sales.ts` | 订单来源、币种、税、FOB、EXW |
| `src/constant/file-type.ts` | `frontend/qmy-admin/src/constant/file-type.ts` | 图片上传类型 |
| `src/views/sed/router/async-modules/sales.ts` | `frontend/qmy-admin/src/views/sed/router/async-modules/sales.ts` | 来源销售管理路由，快照已裁剪为 `/sales/quotation` |

## 未复制或作为依赖处理

| 内容 | 处理 | 原因 |
| :--- | :--- | :--- |
| qmy-admin 全局布局、axios、下载、auth、validate、user store | 依赖目标前端基座 | 属于管理后台公共能力 |
| `@/components/sed-product-selector`、`remote-autocomplete` | 依赖产品物料和前端基座 | 不是报价私有组件 |
| qmy-java 产品、客户、文件、字典完整后端 | 依赖 `product-material`、`customer-management`、`file-oss`、`base-data` | 避免把相关组件整体复制进报价组件 |
| qmy-java 完整订单后端 | 依赖后续 `order-management` | 本组件只保留转订单引用证据 |
| qmy-java 完整 DDL | 未找到来源 | 以结构说明替代 |
| `.DS_Store`、`.git/`、`target/`、`node_modules/`、`build/`、`dist/`、环境配置 | 未复制 | 污染文件或密钥风险 |

## 事实与判断

- `zhongsheng-AI` PRD 与 `QuoteController` 是基础 CRUD 事实，适合最小报价单。
- qmy-admin 报价页与 qmy-java `SalSedQuotationController` 路径匹配，是复杂报价流的主要事实来源。
- 前端路由来源同时包含客户、报价和订单，快照只保留报价入口，客户和订单入口由对应组件提供。
- 复杂流以 `sal_sed_quotation` 为主表，`sal_sed_quotation_sku` 保存每个产品/SKU 的报价时点，`sal_sed_quotation_sku_packing` 保存包材成本，`sal_sed_quotation_history` 保存操作记录。
- 采购成本来自包材、配件和零件聚合；成本明细还会计算配件成本、工艺/油漆成本和包材单件成本。
- 历史报价会按 SKU 和搭配计算均价、中位数、毛利率和趋势，美元报价参与均价/中位数时按汇率折算人民币。
- 转订单会把报价主档、客户、收货地址、币种、税、FOB、EXW、汇率、SKU、包材和报价价格写入订单链路。
- 来源数据权限不闭环：列表有 `@RequiresDataPermissions`，但按 id 详情、成本确认、审核、历史报价和转订单仍需目标项目补报价归属与客户范围守卫。
