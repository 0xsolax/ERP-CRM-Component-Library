# SOURCE MAP

## 来源摘要

| 类型 | 路径 | 用途 | 处理方式 |
| :--- | :--- | :--- | :--- |
| legacy 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/CustomerController.java` | 简版客户 CRUD 和 owner 过滤参考 | 复制为快照 |
| legacy 数据 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` | `customer` 表与扩展字段 | 抽取为 `db/zhongsheng-AI/init-customer.sql` |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer` | 一唐客户列表、详情、新增编辑和扩展弹窗 | 复制为快照 |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/warehouse-history` | 客户详情独立仓历史路由依赖 | 复制为快照 |
| 前端 | `RAW/PROJECTs/qmy-admin/src/views/sed/sales/customer` | 盛尔达客户列表、详情、新增编辑 | 复制为快照 |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts` | `/sal/yt/customer/*` API 封装 | 复制为快照 |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/sed/sales/customer.ts` | sed 客户页面 API 封装 | 复制为快照 |
| 前端常量 | `RAW/PROJECTs/qmy-admin/src/constant/yitang/warehouse.ts` | 独立仓历史页业务类型枚举 | 复制为快照 |
| 补充后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtCustomerController.java` | 与 qmy-admin API 匹配的客户后端 | 复制为快照 |
| 补充后端 | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sal/yt/SalYtCustomerManager.java` | 客户主档、地址、联系人、标签、跟进业务实现 | 复制为快照 |
| 补充后端 | `RAW/PROJECTs/qmy-java/dao/src/main/resources/mapper/sal/yt` | 客户相关 MyBatis SQL 和数据范围字段 | 复制为快照 |
| 权限证据 | `RAW/PROJECTs/qmy-java/*RequiresDataPermissions*`、`AuthenticationInterceptor`、`TenantInterceptor` | 客户数据范围实现证据 | 复制为快照 |
| 原始调研 | `RAW/docs/zhongsheng` | 客户整合、跟进提醒、客户标签、老板视角业务意图 | 复制关键材料 |
| wiki 页面 | `wiki/COMP_客户管理.md` | 编译说明和索引入口 | 更新 |

## 抽取范围

已抽取：

- `zhongsheng-AI` 简版客户 CRUD、实体、Service、Mapper 和 legacy `customer` SQL。
- `qmy-admin` admin/sed 客户列表、详情、新增编辑、联系人、地址、跟进、标签、自动层级、独立仓相关前端。
- `qmy-admin` 客户 API、客户规格 API、客户相关销售路由权限、客户类型/层级常量和独立仓历史页依赖；路由快照已裁剪掉订单/报价等非客户页面入口。
- `qmy-java` 客户主档、地址、联系人、联系人电话、联系人社交账号、跟进、客户标签、客户规格对照、独立仓、数据权限相关后端证据。
- `RAW/docs/zhongsheng` 中与客户管理直接相关的会议记录和需求点。

未抽取：

- `qmy-java` 完整订单、报价、仓储、采购链路，仅保留客户组件直接引用的客户侧代码。
- 管家婆、平台导入、爬虫和外部接口同步实现；仅在文档中列为可选能力和边界。
- 完整 `qmy-java` 建表 SQL，因为来源仓未提供客户相关 DDL。
- `qmy-admin` 全局布局、表格、权限指令、动态弹窗、axios、store、region API 等基础依赖，默认由目标项目基座提供。

待验证：

- 目标项目是否需要一唐 admin 和盛尔达 sed 两套客户页面同时接入。
- 客户独立仓、客户产品规格对照、VIP 客户和消费统计是否属于本项目首期范围。
- 当前快照已补齐核心客户子资源写接口的方法级权限和客户范围校验；正式接入时需确认目标项目是否同步采用该补丁。
- `zhongsheng-AI` legacy `Customer.owner/follower` 与 `init.sql` 缺列问题应如何迁移。

## 清洗规则

- 未复制嵌套 `.git/`。
- 未复制 `.DS_Store`、`target/`、`node_modules/`、构建缓存。
- 未复制环境配置、数据库连接、OSS AccessKey、Token 或私钥文件。
- 保留源码注解、权限码、表名、字段名和接口路径，避免抹掉业务事实。

## 事实与推断

### 已确认事实

- legacy `CustomerController` 暴露 `/api/customer/page`、`/{id}`、POST、PUT、DELETE，并通过 `isAdmin` 和 `username` 做管理员/owner 过滤。
- legacy `Customer` 实体存在 `owner`、`follower` 字段，但 legacy `init.sql` 的 `customer` 表没有对应列。
- `qmy-admin` 客户页面调用 `/sal/yt/customer/list`、`detail`、`save`、`update`、地址、联系人、标签、跟进、独立仓、消费统计等接口。
- `qmy-java` `SalYtCustomerController` 的 base path 为 `api/sal/yt/customer`，与前端 `/sal/yt/customer/*` API 语义匹配。
- `qmy-java` 客户列表、详情、下拉选择使用 `@RequiresDataPermissions`，条件为 `belong_employee_id OR follow_employee_id`。
- 当前快照已将客户主档修改/删除、地址、联系人、标签、跟进写接口纳入同一客户数据范围校验；这属于对原始 qmy-java 快照的审查后加固。
- 调研资料明确客户需要标签、消费情况、订单情况、跨平台客户整合和跟进提醒。

### 推断

- qmy-admin 中 `/sal/yt/customer/*` 在运行时应经过代理或统一前缀映射到 qmy-java 的 `api/sal/yt/customer/*`。
- “老板视角”在 qmy-java 中不是独立客户接口，而是通过 `*` 权限、全数据权限或未开启数据权限配置获得全局数据范围。

### 待验证问题

- 客户标签是否继续复用 `pro_yt_product_label`，还是在新项目拆成独立 `customer_label` 表。
- 客户跟进提醒是否只保留记录，还是需要任务/日历/消息提醒闭环。
- 客户导入是否只做 Excel 导入，还是接平台 API 或授权同步。
