# SYNTH SOL52 客户报价订单文件装配演练

## 结论

本次用 `customer-management`、`quote-management`、`order-management`、`file-oss` 做人工装配演练，结论是：组件库已经能指导新项目判断应复制哪些代码、补哪些表、接哪些接口，但不能只按四个组件机械复制。

实际装配必须显式补入 `BASE/project-scaffold`、`auth-permission`、`base-data` 和 `product-material`。其中 `file-oss` 是上传基础设施，`customer-management` 是销售主数据，`quote-management` 和 `order-management` 是业务链路组件。

## 推荐装配顺序

| 顺序 | 组件 | 作用 | 装配判断 |
| :--- | :--- | :--- | :--- |
| 1 | `BASE/project-scaffold` | 后端分层、租户、基础工程结构 | 必选 |
| 2 | `auth-permission` | 登录、角色、菜单、方法权限、数据权限基础 | 必选 |
| 3 | `base-data` | 报价币种、含税、FOB、EXW、业务字典 | 复杂报价必选 |
| 4 | `product-material` | 产品、规格、包材、图片和成本基础 | 报价和订单必选 |
| 5 | `file-oss` | OSS STS、上传组件、`system_file` 文件记录 | 报价附件、客户附件、产品图片建议先接 |
| 6 | `customer-management` | 客户主档、联系人、地址、标签、跟进、客户数据权限 | 报价和订单前置 |
| 7 | `quote-management` | 报价 CRUD、成本、历史、审核、导出、转订单 | 按简单/复杂流选择模型 |
| 8 | `order-management` | 订单、子订单、商品项、状态、采购申请、退货、发货边界 | 报价转订单后接 |

## 装配路径

### 文件能力

复制或改造：

- `COMPONENTS/file-oss/backend/zhongsheng-backend`
- `COMPONENTS/file-oss/frontend/qmy-admin/src/components/bz-upload`
- `COMPONENTS/file-oss/frontend/qmy-admin/src/api/admin/system/storage.ts`
- `COMPONENTS/file-oss/db/zhongsheng-backend/init-system-file.sql`
- `COMPONENTS/file-oss/db/zhongsheng-backend/init-tenant.sql`

补齐：

- OSS/STS 租户配置。
- `file:oss:token`、`file:oss:save` 方法权限。
- 上传路径租户前缀、文件大小/类型/数量限制。
- 业务表保存 `system_file.id` 的字段或关联表。

### 客户能力

复制或改造：

- `COMPONENTS/customer-management/backend/qmy-java`
- `COMPONENTS/customer-management/frontend/qmy-admin`
- `COMPONENTS/customer-management/docs/contracts`
- 简版项目可参考 `backend/zhongsheng-AI` 和 `db/zhongsheng-AI/init-customer.sql`

补齐：

- qmy-java 客户正式 DDL。
- 客户主档、联系人、地址、标签、跟进表。
- 本人、部门、全公司、老板视角数据范围。
- 报价和订单选择客户时复用同一客户范围。

### 报价能力

复制或改造：

- 简版报价：`COMPONENTS/quote-management/backend/zhongsheng-AI` 和 `db/zhongsheng-AI/init-quote.sql`
- 复杂报价：`COMPONENTS/quote-management/backend/qmy-java`、`frontend/qmy-admin/src/views/sed/sales/quotation`
- `COMPONENTS/quote-management/docs/contracts`

补齐：

- 复杂报价正式 DDL。
- 报价详情、成本、审核、历史、导出、转订单的数据范围守卫。
- 报价时点快照：客户、地址、产品、SKU、包材、汇率、价格、成本。
- 转订单幂等和重复转换防护。
- 微信审核图片和附件使用 `file-oss`。

### 订单能力

复制或改造：

- 简版订单：`COMPONENTS/order-management/backend/zhongsheng-AI` 和 `db/zhongsheng-AI/init-order.sql`
- 复杂订单：`COMPONENTS/order-management/backend/qmy-java`、`frontend/qmy-admin/src/views/admin/sales/order`
- `COMPONENTS/order-management/docs/contracts`

补齐：

- 复杂订单正式 DDL。
- 订单详情、退货、发货、关闭、导出、采购申请的数据范围守卫。
- `/delete`、`/confirmComplete` 等状态变更 GET 方法收口。
- 订单状态机、操作记录、采购/仓储/财务边界。
- 报价转订单来源追溯。

## 本次发现的关键缺口

| 优先级 | 缺口 | 影响 |
| :--- | :--- | :--- |
| P1 | 四组件组合实际还依赖 `base-data`、`product-material`，不能只装配 SOL-52 点名的四个组件 | 否则报价/订单页面无法解释字典、产品、规格、包材和成本 |
| P1 | `quote-management`、`order-management` 的 qmy-java 复杂流缺正式 DDL | 不能直接迁移数据库 |
| P1 | 报价详情/成本/历史/转订单、订单详情/退货/发货/关闭/导出存在数据范围守卫缺口 | 有越权读取和越权状态变更风险 |
| P1 | 订单存在状态变更 GET；报价转订单缺运行级幂等证据 | 有审计和重复生成风险 |
| P2 | `file-oss` 运行验收依赖真实 OSS/STS 配置和测试 bucket | 快照无法证明上传链路已在目标项目可用 |
| P2 | 前端共享依赖需要基座统一提供 | 直接复制页面会缺布局、权限指令、请求封装、产品选择器、上传组件等 |
| P2 | 当前只有组件级验收，缺跨组件真实账号回归 | 不能证明本人/部门/全公司/老板视角在报价和订单链路完全一致 |

## 装配验收清单

- [x] 能从组件文档判断复制路径。
- [x] 能从契约判断需要补哪些表。
- [x] 能从 API 契约判断需要接哪些接口。
- [x] 能识别必须补入的非 SOL-52 前置组件。
- [x] 能明确运行前不可跳过的权限、DDL、状态机和文件配置缺口。
- [ ] 目标项目执行真实 DDL。
- [ ] 目标项目完成真实账号接口回归。
- [ ] 目标项目完成浏览器端客户、报价、订单、上传主链路回归。

## 后续建议

- 将本页作为“客户 -> 报价 -> 订单 + 文件”装配入口。
- 若后续做真实项目装配，应先选择“简版 CRUD”还是“qmy-java 复杂流”，不要混用两套模型。
- `purchase-supplier`、`warehouse-delivery`、`finance` 应作为订单履约后续阶段，不纳入本次最小装配闭环。
