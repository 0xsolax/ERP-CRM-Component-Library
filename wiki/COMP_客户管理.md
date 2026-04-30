# COMP 客户管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | CRM / 销售 |
| 复用等级 | 可参考改造 |
| 组件快照 | `COMPONENTS/customer-management` |
| 适用项目 | B2B 销售、外贸、订单型制造企业 |
| 来源路径 | `RAW/PROJECTs/zhongsheng-AI/erp-backend`、`RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer`、`RAW/PROJECTs/qmy-admin/src/views/admin/sales/warehouse-history`、`RAW/PROJECTs/qmy-admin/src/views/sed/sales/customer`、`RAW/PROJECTs/qmy-java`、`RAW/docs/zhongsheng` |

## 业务目标

维护客户档案、联系人、地址、跟进记录、标签、客户类型/层级和订单消费情况，为报价、订单、客户回访、客户分层和销售数据权限提供基础。

## 前端入口

- 一唐客户：`RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer/index.vue`、`add.vue`、`detail.vue`。
- 一唐独立仓历史：`RAW/PROJECTs/qmy-admin/src/views/admin/sales/warehouse-history/index.vue`。
- 盛尔达客户：`RAW/PROJECTs/qmy-admin/src/views/sed/sales/customer`。
- API 封装：`RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`、`RAW/PROJECTs/qmy-admin/src/api/sed/sales/customer.ts`。
- 路由权限：`RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/sales.ts`、`RAW/PROJECTs/qmy-admin/src/views/sed/router/async-modules/sales.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| legacy 客户分页 | GET | `/api/customer/page` | `zhongsheng-AI CustomerController` |
| legacy 客户 CRUD | GET/POST/PUT/DELETE | `/api/customer/*` | `zhongsheng-AI CustomerController` |
| 客户列表 | POST | `/sal/yt/customer/list` | `qmy-admin` + `qmy-java SalYtCustomerController` |
| 客户详情 | GET | `/sal/yt/customer/detail` | `qmy-admin` + `qmy-java SalYtCustomerController` |
| 新增客户 | POST | `/sal/yt/customer/save` | `qmy-admin` + `qmy-java SalYtCustomerController` |
| 编辑客户 | POST | `/sal/yt/customer/update` | `qmy-admin` + `qmy-java SalYtCustomerController` |
| 联系人/地址/标签/跟进 | POST/GET | `/sal/yt/customer/*` | `qmy-admin` + `qmy-java SalYtCustomerController` |
| 客户下拉 | POST | `/sal/yt/customer/selectList` | 报价、订单选客户入口 |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `customer` | `code`、`name`、`contact`、`phone`、`email`、`address`、`status`、`type`、`level`、`country` | legacy 基础客户表 |
| `sal_yt_customer` | `code`、`name`、`belong_employee_id`、`follow_employee_id`、`type`、`hand_level`、`country_region`、`company_website`、`company_address`、`order_default_remark` | qmy-java 客户主档 |
| `sal_yt_customer_address` | `customer_id`、`consignee`、`phone`、`country_region`、`province`、`city`、`county`、`detail` | 收货地址 |
| `sal_yt_contact_person` | `customer_id`、`name`、`email`、`position`、`birthday`、`gender`、`remark` | 联系人 |
| `sal_yt_contact_person_phone` / `sal_yt_contact_person_social` | `contact_id`、`phone`、`social_platform`、`value` | 联系人电话和社交账号 |
| `sal_yt_customer_follow` | `customer_id`、`theme`、`contact_person`、`description`、`next_visit_date` | 跟进记录 |
| `pro_yt_product_label` | `master_id`、`value`、`type=4` | 客户标签 |

## 权限边界

- legacy `CustomerController` 按请求中的 `isAdmin` 和 `username` 做管理员/owner 过滤，但 legacy SQL 未提供 `owner` 字段。
- qmy-java 列表、详情、下拉选择使用 `@RequiresDataPermissions`，以 `belong_employee_id OR follow_employee_id` 过滤。
- 当前组件快照已补齐客户主档修改/删除，以及地址、联系人、标签、跟进写接口的后端权限和客户范围校验。
- 本人：当前用户是归属业务员或跟进人。
- 部门：当前用户是部门负责人时，额外包含部门成员。
- 全公司：用户数据权限为全部数据或租户未启用该权限过滤。
- 老板视角：业务上应映射为全公司数据权限和角色权限，来源未见独立老板接口。

## 接入步骤

1. 确认客户来源：手工录入、Excel 导入、平台接口、授权同步或爬虫。
2. 确认客户主档字段、联系人、地址、标签、跟进是否全部进入首期。
3. 建立客户主档、联系人、地址、标签、跟进数据结构。
4. 接入客户列表、详情、新增、编辑、删除、客户下拉。
5. 与报价、订单组件共享 `customer_id` 和客户地址。
6. 按组织和角色配置本人、部门、全公司、老板视角数据范围。
7. 对照 `COMPONENTS/customer-management/docs/contracts/` 做 API、数据、权限复核。

## 验收清单

- [x] 客户列表支持客户编号、客户名称、归属业务员过滤。
- [x] 客户详情能看到联系人、地址、跟进、标签。
- [x] 新增客户可同时保存地址、联系人和标签。
- [x] 非全局用户数据范围受限。
- [x] 客户能被报价单和订单引用，且客户下拉不越权。
- [x] 客户导入或外部平台同步策略有明确边界。
- [x] review 证据已进入 `COMPONENTS/customer-management/docs/acceptance/REVIEW_EVIDENCE.md`。

## 已知风险

- legacy `Customer.owner/follower` 与 `init.sql` 缺列不一致。
- qmy-java 建表 SQL 未随来源提供，正式接入需重新生成迁移。
- qmy-java 原始来源中部分客户子资源接口的后端权限注解被注释；当前快照已补核心写接口，独立仓扩展接口仍需按项目范围复核。
- 调研资料强调跨平台客户整合和跟进提醒，但导入/同步/爬虫不能默认打开。
