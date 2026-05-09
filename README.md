# ERP-CRM-Component-Library

## 项目定位

本仓用于沉淀 ERP/CRM 项目的可复用组件知识库。

目标不是单纯保存历史代码，而是把历史项目中的业务能力、前后端接口、数据结构、权限边界、规范文档和验收标准整理成可检索、可追溯、可装配的资产。后续新项目进来时，可以先按客户需求选择基座与组件，再按契约快速装配。

当前阶段以 Markdown 知识库和代码快照为主，不先做自动代码生成器，也不急于抽 npm/Maven 包。

## 核心分层

| 层级 | 目录 | 说明 |
| :--- | :--- | :--- |
| 来源层 | `RAW/` | 历史项目源码、PRD、会议记录、README 等原始证据；默认只读 |
| 基座层 | `BASE/` | 可作为新项目母版的基座快照，包含代码、来源、规范、契约和验收 |
| 组件层 | `COMPONENTS/` | 可装配的 ERP/CRM 全链路组件快照 |
| 知识层 | `wiki/` | 可共享知识库入口、项目页、组件卡、来源索引和维护日志 |
| 执行层 | `pdoc/` | 本地计划、规则、报告、经验日志；只在本机排除，不写入 `.gitignore` |

## 当前资产

### 基座

| 名称 | 状态 | 入口 |
| :--- | :--- | :--- |
| `project-scaffold` | `reference` | [BASE/project-scaffold](BASE/project-scaffold/README.md) |

### 组件

| 名称 | 状态 | 入口 | 说明 |
| :--- | :--- | :--- | :--- |
| `auth-permission` | `reference` | [COMPONENTS/auth-permission](COMPONENTS/auth-permission/README.md) | 登录、JWT、Token、用户、角色、菜单、权限、前端守卫 |
| `base-data` | `reference` | [COMPONENTS/base-data](COMPONENTS/base-data/README.md) | 基础数据树、字段分类、通用数据行、nodeKey、SQL、字段管理前端 |
| `file-oss` | `reference` | [COMPONENTS/file-oss](COMPONENTS/file-oss/README.md) | OSS STS、文件记录、上传组件、system_file、旧上传控制器对比 |
| `product-material` | `reference` | [COMPONENTS/product-material](COMPONENTS/product-material/README.md) | 产品主档、产品 BOM、伞架、面料、材料、包材、工序、成本字段 |
| `customer-management` | `reference` | [COMPONENTS/customer-management](COMPONENTS/customer-management/README.md) | 客户主档、联系人、地址、标签、跟进、数据权限和导入/同步边界 |
| `quote-management` | `reference` | [COMPONENTS/quote-management](COMPONENTS/quote-management/README.md) | 基础报价 CRUD、SED 复杂报价、成本明细、审核、导出和转订单边界 |
| `order-management` | `reference` | [COMPONENTS/order-management](COMPONENTS/order-management/README.md) | 基础订单 CRUD、YT 复杂订单、状态、采购申请、退货、发货、完结和关闭边界 |
| `purchase-supplier` | `draft` | [COMPONENTS/purchase-supplier](COMPONENTS/purchase-supplier/README.md) | 供应商、待采购、采购单、库存预警、退货、导出和采购/仓储/财务边界 |
| `warehouse-delivery` | `draft` | [COMPONENTS/warehouse-delivery](COMPONENTS/warehouse-delivery/README.md) | 库存、入库、箱规、物流、打包、发货、库存流水和跨组件边界 |
| `document-action` | `reference` | [COMPONENTS/document-action](COMPONENTS/document-action/README.md) | 公共单据日志、锁定、解锁、重新确认、负责人改派和系统操作日志 |
| `production-management` | `reference` | [COMPONENTS/production-management](COMPONENTS/production-management/README.md) | 生产组、生产总单、产品行进度、分批安排、交货和生产单导出 |
| `supplier-inquiry` | `reference` | [COMPONENTS/supplier-inquiry](COMPONENTS/supplier-inquiry/README.md) | 供应商询价台账、历史价格、对象历史、编辑日志和采购前比价资料 |

### 第一批待验证组件

- `purchase-supplier`：已抽取为 `draft`，待补正式 DDL、权限闭环、状态机和运行验收。
- `warehouse-delivery`：已抽取为 `draft`，待补正式 DDL、数据范围权限、库存扣减规则和运行验收。

## 使用方式

新项目启动时，建议按以下顺序使用本仓：

1. 从 [wiki/index.md](wiki/index.md) 查看知识库总入口。
2. 根据客户需求匹配 `wiki/COMP_*.md` 组件卡。
3. 选择基座：优先查看 [BASE/README.md](BASE/README.md)。
4. 选择组件：查看 [COMPONENTS/README.md](COMPONENTS/README.md)。
5. 进入具体组件，依次读取：
   - `README.md`
   - `SOURCE_MAP.md`
   - `docs/spec/`
   - `docs/contracts/`
   - `docs/acceptance/`
6. 根据目标项目技术栈改造代码快照，不直接把快照当成生产 SDK。
7. 按验收清单补齐接口、数据、权限、异常路径和业务边界。

## 组件快照标准

每个可复用组件至少应包含：

| 文件或目录 | 说明 |
| :--- | :--- |
| `component.yaml` | 组件元数据、来源、入口、依赖、状态 |
| `README.md` | 组件定位、能力边界、接入步骤 |
| `SOURCE_MAP.md` | 来源路径、复制范围、排除范围、判断依据 |
| `backend/` | 后端代码快照 |
| `frontend/` | 前端代码快照 |
| `db/` | 表结构、初始化 SQL、迁移片段 |
| `docs/source/` | 原项目规范、README、PRD、接口说明等原文证据 |
| `docs/spec/` | 整理后的组件规范 |
| `docs/contracts/` | API、数据、权限、错误码、状态契约 |
| `docs/acceptance/` | 接入与验收清单 |

## 维护规则

- `RAW/` 默认只读，不直接修历史项目源码。
- `BASE/` 和 `COMPONENTS/` 是经过清洗和说明的快照资产。
- `wiki/` 写可共享结论，必须可追溯到来源。
- `pdoc/` 写本地执行过程、报告和经验，不进入共享 Git。
- 组件缺少前端、后端、数据、权限、验收任一关键项时，不标记为可直接复用。
- 来源文档和代码冲突时，必须标记为待验证，不把推断写成事实。
- 每次新增或修正组件后，同步更新相关 README、`SOURCE_MAP.md`、契约、验收清单和 `wiki/` 入口。

## 协作边界

- Agent 可以修改文件、运行验证、整理报告和给出 Git 操作建议。
- Git 暂存、提交、推送、回滚、合并、打标签等变更性操作由项目负责人执行。
- Agent 只提供建议的文件清单、提交说明和验证结果。
- 禁止 force push。

## 当前状态

| 阶段 | 状态 | 说明 |
| :--- | :--- | :--- |
| P0 目录与模板 | 已完成 | 建立 `BASE/`、`COMPONENTS/`、`COMPONENTS/_TEMPLATE/` |
| P1 基座快照 | 已完成 | 抽取 `project-scaffold` 基座快照 |
| P1 认证权限组件 | 已完成 | 抽取并修正 JWT 安全基线 |
| P1 基础数据组件 | 已完成 | 抽取并修正空请求体、nodeKey 和路由权限边界 |
| P1 文件 OSS 组件 | 已完成 | 抽取 OSS STS、文件记录、上传组件和旧上传控制器对比 |
| P2 业务组件 | 已完成首轮 | 产品物料、客户、报价、订单为 `reference`；采购供应商、仓储发货已抽取为 `draft` |
| P3 待验证组件 | 进行中 | 采购供应商、仓储发货待补正式 DDL、权限闭环、状态机和运行验收 |
| P4 装配演练 | 已完成首轮 | SOL-52 已完成客户 -> 报价 -> 订单 + 文件上传人工装配演练，缺口已记录 |
| P5 qmy 当前实现补充抽取 | 已启动 | 从 `solazhu-finance-0509` 抽取公共单据动作、生产单管理、供应商询价台账三个新组件 |

## 后续更新方式

后续每完成一个阶段，更新本 README 的：

- 当前资产
- 第一批待验证组件
- 当前状态
- 已知限制或维护规则

组件级细节仍写入各组件目录和 `wiki/`，根 README 只保留项目级入口和状态。
