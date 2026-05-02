# ACCEPTANCE｜order-management

## 快照完整性

- [x] `component.yaml` 已填写真实组件名、状态、依赖和入口。
- [x] `README.md` 能说明组件用途、接入步骤和限制。
- [x] `SOURCE_MAP.md` 保留所有关键 `RAW/...` 来源路径。
- [x] `docs/source/` 已带出 PRD 和调研材料。
- [x] `docs/spec/` 已整理组件实现规范。
- [x] `docs/contracts/` 已覆盖 API、数据、权限。
- [x] `docs/acceptance/REVIEW_EVIDENCE.md` 已记录执行与验证证据。

## 代码与数据

- [x] `backend/` 包含 legacy 订单 CRUD 后端快照。
- [x] `backend/` 包含 qmy-java YT 复杂订单 controller/service/manager/entity/mapper/enum 快照。
- [x] `backend/` 包含订单申请采购的 controller/manager/entity/mapper 边界证据。
- [x] `frontend/` 包含订单页面、订单 API、订单常量和裁剪后的路由。
- [x] 客户、组织、产品依赖 API 已裁剪为订单页面实际使用函数，避免跨模块能力污染。
- [x] `frontend/` 已补入订单列表直接使用的 `interface/table.ts` 类型文件。
- [x] `db/` 包含 legacy `orders/order_item` SQL 和 qmy-java 复杂表结构说明。
- [x] 缺失的 qmy-java 完整 DDL、数据范围守卫和 HTTP 方法风险已明确标记为待补齐。

## 业务验收

- [x] 订单主档、订单明细、订单状态、来源报价字段已明确。
- [x] YT 复杂订单的子订单、商品项、退货、半成品确认、发货、完结、关闭和导出已明确。
- [x] 采购申请边界已明确：订单只生成采购需求，采购单后续归采购组件。
- [x] 仓储/物流边界已明确：订单展示或触发发货确认，完整入库、打包、包裹、物流归仓储组件。
- [x] 财务边界已明确：订单展示回款/利润状态，真实财务流水归财务组件。
- [x] 已标注哪些能力有后端实现证据，哪些是前端 API 或跨组件边界证据。

## 污染检查

- [x] 不包含 `.git/`。
- [x] 不包含 `.DS_Store`。
- [x] 不包含 `target/`。
- [x] 不包含 `node_modules/`。
- [x] 不包含 `dist/`、`build/`。
- [x] 不包含 `.env` 或本地运行配置。
- [x] 不包含数据库密码、OSS AccessKey、私钥等敏感信息。

## 装配验收

- [x] 能说明依赖哪些基础组件。
- [x] 能说明接入顺序。
- [x] 能说明最小可运行路径。
- [x] 能说明主要异常和权限边界。
- [x] 能说明不能直接复用的风险。

## 接入前必须补齐

- [ ] qmy-java 复杂订单正式 DDL、索引、唯一键和软删除字段。
- [ ] 详情、退货、发货、完结、关闭、导出、采购申请等接口的数据范围守卫。
- [ ] `/delete`、`/confirmComplete` 等状态变更 GET 接口的方法收口。
- [ ] 报价转订单幂等、字段快照和重复转换防护。
- [ ] 本人、部门、全公司、老板视角的真实账号回归。
- [ ] 新项目若直接编译 qmy-admin 页面，需先接入 `footer-actions`、`product-selector`、`tagsStore`、`bz-table`、`utils` 等前端基座/共享依赖。
