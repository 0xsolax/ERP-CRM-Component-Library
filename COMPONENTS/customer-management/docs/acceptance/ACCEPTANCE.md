# ACCEPTANCE｜customer-management

## 快照完整性

- [x] `component.yaml` 已填写真实组件名、状态、依赖和入口。
- [x] `README.md` 已说明组件用途、接入步骤和限制。
- [x] `SOURCE_MAP.md` 已保留关键 `RAW/...` 来源路径。
- [x] `docs/source/` 已带出客户管理相关调研材料。
- [x] `docs/spec/` 已整理组件实现规范。
- [x] `docs/contracts/` 已覆盖 API、数据、权限。

## SOL-47 验收点

- [x] 客户主档已标注：legacy `customer` 和 qmy-java `sal_yt_customer` 双来源。
- [x] 联系人已标注：`sal_yt_contact_person`、电话、社交账号和前端弹窗均有证据。
- [x] 地址已标注：`sal_yt_customer_address` 和前端地址弹窗均有证据。
- [x] 标签已标注：`pro_yt_product_label` 复用，客户标签 `type = 4`。
- [x] 跟进已标注：`sal_yt_customer_follow`、跟进弹窗和附件类型均有证据。
- [x] 已明确数据权限：本人、部门、全公司、老板视角。
- [x] 已明确导入/平台同步不是默认能力，需要项目确认。

## 代码与数据

- [x] `backend/` 只包含客户管理相关源码和必要权限证据。
- [x] `frontend/` 包含页面、API、路由和枚举常量。
- [x] `db/` 包含 legacy 客户 SQL 和 qmy-java 数据契约说明。
- [x] 已标记 qmy-java 建表 SQL 缺失。
- [x] 已标记 legacy `Customer.owner/follower` 与 SQL 缺列问题。

## 污染检查

- [x] 不包含 `.git/`。
- [x] 不包含 `.DS_Store`。
- [x] 不包含 `target/`。
- [x] 不包含 `node_modules/`。
- [x] 不包含 `build/`、`dist/`。
- [x] 不包含环境配置、Token、数据库密码、OSS AccessKey 或私钥。

## 装配验收

- [x] 客户列表支持客户编号、客户名称、归属业务员过滤。
- [x] 客户详情可返回主档、联系人、地址、标签和跟进。
- [x] 新增客户可同时保存地址、联系人和标签。
- [x] 编辑客户主档、联系人、地址、标签、跟进均有后端权限。
- [x] 非全局用户只能看到本人或部门范围客户。
- [x] 老板/全公司视角必须通过角色和数据权限显式配置。
- [x] 报价、订单引用客户时不能越权选客户。
- [x] 客户导入、平台同步、爬虫采集策略已被项目负责人确认。

## 复核补齐

- [x] P1 客户子资源写接口已补方法级权限和客户主档数据范围校验。
- [x] P2 复核证据已进入可跟踪组件目录：`docs/acceptance/REVIEW_EVIDENCE.md`。
- [x] P2 客户组件路由已裁剪订单、报价和独立仓历史等非最小客户档案入口。
- [x] P1 删除客户、地址、联系人、标签、跟进接口已由 GET 改为 DELETE。

说明：以上为组件快照级装配验收。完整运行时验收需在目标项目接入后，用真实账号验证本人、部门、全公司、老板视角。
