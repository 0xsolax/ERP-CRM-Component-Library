# COMP 客户管理

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | CRM / 销售 |
| 复用等级 | 可参考改造 |
| 适用项目 | B2B 销售、外贸、订单型制造企业 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer`、`RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/CustomerController.java`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`、`RAW/docs/zhongsheng` |

## 业务目标

维护客户档案、联系人、地址、跟进记录、标签、客户类型/层级和订单消费情况，为报价、订单、跟进提醒和客户分层服务。

## 前端入口

- 一唐客户：`RAW/PROJECTs/qmy-admin/src/views/admin/sales/customer/index.vue`、`add.vue`、`detail.vue`。
- 盛尔达客户：`RAW/PROJECTs/qmy-admin/src/views/sed/sales/customer`。
- API 封装：`RAW/PROJECTs/qmy-admin/src/api/admin/sales/customer.ts`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 客户分页 | GET | `/api/customer/page` | `CustomerController` |
| 客户详情 | GET | `/api/customer/{id}` | `CustomerController` |
| 新增客户 | POST | `/api/customer` | `CustomerController` |
| 编辑客户 | PUT | `/api/customer` | `CustomerController` |
| 删除客户 | DELETE | `/api/customer/{id}` | `CustomerController` |
| 客户列表 | POST | `/sal/yt/customer/list` | `qmy-admin` API |
| 联系人/地址/标签/跟进 | POST/GET | `/sal/yt/customer/*` | `qmy-admin` API |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `customer` | `code`、`name`、`contact`、`phone`、`email`、`address`、`status` | 基础客户档案 |
| `customer` 扩展 | `type`、`level`、`country` | 客户类型、层级、国家地区 |
| 联系人/地址/标签 | 前端 API 有调用，旧 SQL 未见独立表 | 需要新项目补表或对齐已有后端 |

## 权限边界

- 旧 `CustomerController` 按请求中的 `isAdmin` 和 `username` 做 owner 过滤：管理员看全部，非管理员按用户名过滤。
- 新项目应明确客户数据范围：本人、部门、全公司、老板全局视角。

## 接入步骤

1. 先确认客户来源：手工录入、Excel 导入、平台接口、授权爬取。
2. 确认客户主档字段和标签体系。
3. 对齐联系人、地址、跟进记录、客户层级是否需要独立表。
4. 接入列表、详情、新增、编辑、删除或停用。
5. 与报价、订单组件共享 `customer_id`。

## 验收清单

- [ ] 客户列表支持关键字搜索。
- [ ] 客户详情能看到联系人、地址、跟进、标签。
- [ ] 非管理员数据范围受限。
- [ ] 客户能被报价单和订单引用。
- [ ] 客户导入或外部平台同步策略有明确边界。

## 已知风险

- 调研资料强调跨平台客户整合和跟进提醒，但当前代码证据只覆盖部分 CRUD 和前端扩展 API。
- 新项目不能把“爬取客户数据”作为默认能力，必须先确认授权和平台规则。

