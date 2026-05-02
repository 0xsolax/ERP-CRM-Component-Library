# ACCEPTANCE｜quote-management

复核证据见：[REVIEW_EVIDENCE.md](REVIEW_EVIDENCE.md)。

## SOL-48 快照结论

- [x] 快照级验收已完成：基础 CRUD、复杂报价流、前端页面、API、数据契约、权限边界、来源映射和污染扫描均已闭环。
- [x] 已明确区分 `zhongsheng-AI` 基础报价 CRUD 与 qmy-java/qmy-admin SED 复杂报价流。
- [x] 成本明细、历史报价、审核、转订单均有来源标注。
- [x] 客户敏感信息、成本、毛利率、历史报价参考和导出边界已写入权限契约。
- [x] qmy-java 数据权限未闭环作为接入风险记录，未被包装成已安全生产实现。

## 快照验收

- [x] `backend/zhongsheng-AI/` 包含 legacy `QuoteController`、`Quote`、`QuoteService`、`QuoteServiceImpl`、`QuoteMapper`。
- [x] `backend/qmy-java/` 包含 SED 报价 controller、service、manager、entity、param、VO、Mapper 和 Mapper XML。
- [x] `frontend/qmy-admin/` 包含 SED 报价页面、API、状态常量、销售路由入口和直接依赖 API。
- [x] `db/` 包含 legacy `quote/quote_item` SQL 和 qmy-java 表结构说明。
- [x] `docs/source/` 包含 PRD 和调研材料来源副本。
- [x] `docs/contracts/` 覆盖 API、数据、权限契约。
- [x] `SOURCE_MAP.md` 标明来源、已复制范围、依赖和已知缺口。
- [x] 不包含 `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/`。
- [x] 不包含真实数据库连接、token、密钥或本地环境配置。

## 能力验收

- [x] 基础 CRUD 与复杂报价流差异已写入 `README.md`、`SOURCE_MAP.md` 和 `COMPONENT_SPEC.md`。
- [x] 成本明细来源已覆盖 `getCostDetail`、`procurementDetail`、`logisticsDetail` 和 manager 成本计算。
- [x] 历史报价来源已覆盖 `getHistoryQuotation`、`getHistoryQuotationDetail`、`getHistoryQuotationInfo`。
- [x] 审核来源已覆盖 `submitAudit`、`audit`、`jointAudit`、`presidentWxAudit`。
- [x] 转订单来源已覆盖 `oneKeyToOrder`、`skuToOrder`、`mergeList`、`mergeSkuList`、`mergeToOrder`。
- [x] 客户敏感信息和报价参考边界已写入 `PERMISSION_CONTRACT.md`。

## 接入验收（后续装配演练）

- [ ] 已在目标项目中执行报价表 DDL。
- [ ] 已接入产品物料、客户管理、基础数据、文件和订单依赖。
- [ ] 已登录有权限用户可以创建报价并保存至少一个 SKU。
- [ ] 报价详情能回显 SKU、包材、成本、历史操作和审核信息。
- [ ] 采购成本和物流成本可按角色确认。
- [ ] 历史报价能按 SKU 和搭配返回均价、中位数、趋势和毛利率。
- [ ] 审核、会签、微信审核和驳回状态流转符合业务规则。
- [ ] 整单、单 SKU、合并转订单能保留报价来源。
- [ ] 无权限用户访问详情、成本、历史报价、导出和转订单接口被后端拒绝。
- [ ] 导出结果只包含当前用户有权访问的报价。

## 当前快照未运行验证

- 未编译后端快照，因为 qmy-java 复杂流依赖完整产品、客户、文件、字典、订单和项目基座。
- 未执行前端浏览器流程，因为该目录是组件快照，不是独立 Vite 应用。
- 未执行数据库迁移，因为 qmy-java 来源没有完整报价 DDL。
