# customer-management 客户管理组件

## 定位

`customer-management` 提供 ERP/CRM 销售链路中的客户主档、联系人、收货地址、客户标签、跟进记录、客户层级和客户数据范围能力。组件目标是让新项目能快速判断客户管理需要哪些表、页面、接口、权限和边界，而不是直接把历史项目代码当成可运行包。

本组件是可追溯快照，状态为 `reference`。接入新项目时必须先确认客户来源、销售组织结构、数据权限口径和导入/同步策略。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 业务组件 |
| 依赖组件 | `BASE/project-scaffold`、`auth-permission` |
| 可选依赖 | `file-oss`、`product-material`、订单/报价组件 |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-admin`、`RAW/PROJECTs/qmy-java`、`RAW/docs/zhongsheng` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-AI/` | 旧中圣简版客户 CRUD：`CustomerController`、`Customer`、`CustomerService`、`CustomerMapper` |
| `backend/qmy-java/` | 与 qmy-admin `/sal/yt/customer/*` 匹配的客户、联系人、地址、标签、跟进、独立仓、数据权限证据 |
| `frontend/qmy-admin/` | 一唐 admin 与盛尔达 sed 客户列表、详情、新增编辑、弹窗组件、客户 API、路由、客户常量和独立仓历史页依赖 |
| `db/` | legacy `customer` 表 SQL 和 qmy-java 表结构说明 |
| `docs/source/` | 中圣调研中关于客户整合、跟进提醒、客户标签、老板视角的原始材料 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照和接入验收清单 |

## 能力边界

已覆盖：

- 客户主档：客户编号、客户名称、归属业务员、跟进人、客户类型、手动层级、自动层级、国家地区、官网、公司地址、默认订单备注。
- 联系人：联系人姓名、邮箱、职位、生日、性别、备注、名片/头像、社交账号、电话列表。
- 收货地址：收货人、电话、国家地区、省市县、详细地址、默认占位地址。
- 标签：复用 `pro_yt_product_label`，客户标签类型为 `LabelTypeEnum.customerLabel = 4`。
- 跟进记录：主题、联系人、行动描述、下次回访日期、附件。
- 消费信息：列表/详情侧补充最近跟进时间、近一年累计金额、最近下单时间、消费趋势、消费占比。
- 前端入口：客户列表、客户新增/编辑、客户详情、联系人/地址/跟进弹窗、自动客户层级配置。
- 数据权限：`qmy-java` 对客户列表、详情、下拉选择、客户主档修改/删除，以及地址、联系人、标签、跟进写接口按 `belong_employee_id`、`follow_employee_id` 做客户范围校验。

待项目确认：

- `zhongsheng-AI` legacy 后端 `Customer` 实体包含 `owner`、`follower`，但 `init.sql` 的 `customer` 表未定义这两个字段；该实现不能直接作为可运行 schema 使用。
- `qmy-java` 没有随源码提供完整建表 SQL，本快照依据实体、Mapper XML 和前端字段整理数据契约。
- 客户导入、平台同步、爬虫、管家婆同步不是默认能力，必须按平台授权、数据质量和导入频率另行立项。
- 独立仓、客户产品规格对照、VIP 客户、消费统计属于客户管理扩展能力；新项目可选接入，不应默认进入最小客户档案。
- `auth/org`、`system/region`、全局布局、动态弹窗和权限指令属于管理后台基座能力，本组件不复制为客户私有实现。

## 快速接入

1. 先接入 `BASE/project-scaffold` 和 `auth-permission`，确认登录用户、角色权限、部门负责人和数据权限能力。
2. 根据目标项目选择最小模型：只需要客户主档时参考 `zhongsheng-AI`；需要联系人、地址、标签、跟进时参考 `qmy-java`。
3. 建立客户主表、联系人、联系人电话、联系人社交账号、地址、跟进、标签关系表。
4. 接入客户列表、详情、新增、编辑、删除、地址、联系人、标签和跟进 API。
5. 前端接入 `qmy-admin` 客户列表、详情、新增编辑页面和弹窗组件。
6. 配置客户权限码：`sal:yt:customer:list`、`detail`、`save`、`update`、`delete`、`updateAddress`、`updateContactPerson`、`setAutoLevel` 等。
7. 明确数据范围：本人、部门、全公司、老板视角分别对应哪些用户和角色。
8. 按 `docs/acceptance/ACCEPTANCE.md` 做客户 CRUD、联系人/地址/标签/跟进、数据权限和导入边界验收。

## 安全与业务规则

- 客户名称、联系人、电话、邮箱、地址属于敏感客户资料；前端脱敏只能改善展示，接口必须做权限和数据范围控制。
- 客户详情接口要返回主档、联系人、地址、标签和跟进时，必须保证所有子数据同属当前客户。
- 客户子资源写接口不能只校验按钮权限；后端必须先验证目标客户在当前用户本人/部门/全公司/老板视角范围内。
- 删除客户应明确逻辑删除和级联逻辑；`qmy-java` 已逻辑删除客户、地址和联系人，但跟进/标签的删除策略需项目确认。
- 列表、详情、客户下拉必须都走同一套数据范围；否则报价/订单里可能选到越权客户。
- 客户导入和平台同步涉及授权、重复客户合并、数据清洗和责任归属，不能作为默认能力自动打开。
