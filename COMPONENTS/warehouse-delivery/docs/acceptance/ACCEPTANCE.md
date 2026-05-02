# ACCEPTANCE｜warehouse-delivery

## 快照完整性

- [x] `component.yaml` 已填写真实组件名、状态、依赖和入口。
- [x] `component.yaml` 按 SOL-51 要求标记 `status: draft`。
- [x] `README.md` 能说明组件用途、接入步骤和限制。
- [x] `SOURCE_MAP.md` 保留所有关键 `RAW/...` 来源路径。
- [x] `docs/source/` 已带出调研材料。
- [x] `docs/spec/` 已整理组件实现规范。
- [x] `docs/contracts/` 已覆盖 API、数据、权限。
- [x] `docs/acceptance/REVIEW_EVIDENCE.md` 已记录执行与验证证据。

## 代码与数据

- [x] `frontend/` 包含仓储、发货、物流、打包页面与 API。
- [x] `frontend/` 包含启用中的 `warehouse.ts` 路由。
- [x] `frontend/` 保留注释状态的 `delivery.ts` 并标注为 legacy/停用入口。
- [x] `backend/` 包含 qmy-java `sto/yt` controller/manager/entity/param/VO/mapper/event/listener/job/enum 证据。
- [x] `db/` 已明确列出 qmy-java 缺正式 DDL，只提供 schema notes。

## 业务验收

- [x] 库存维度已明确：真实库存、可用库存、占用库存、真实在途、可用在途、占用在途、客户独立仓。
- [x] 入库、出库、采购入库、独立仓出入库边界已整理。
- [x] 打包箱、包裹、包裹明细、扫码打包和完成打包能力已整理。
- [x] 物流公司、运单、确认发货和退回待打包能力已整理。
- [x] 订单、采购、客户、产品、财务和文件组件边界已明确。
- [x] 未确认库存扣减规则前不得标记为可直接复用。

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

- [ ] qmy-java 仓储发货正式 DDL、索引、唯一键、租户字段和软删除字段。
- [ ] 库存扣减、占用释放、客户独立仓和公共仓调拨规则。
- [ ] 所有仓储/发货写接口的数据范围守卫。
- [ ] `box/delete`、`transportCompany/delete` 等破坏性 GET 接口的方法收口。
- [ ] 发货、打包、退回待打包、确认发货的状态机与幂等校验。
- [ ] 订单、采购、财务、文件相关接口联动回归。
