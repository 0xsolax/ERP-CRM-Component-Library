# 维护日志

## 2026-05-02

- 启动并完成 Multica `SOL-50` 本地组件草稿抽取：生成 `COMPONENTS/purchase-supplier`，覆盖 `zhongsheng-AI` 供应商 CRUD、qmy-java YT 供应商/待采购/采购单/库存预警后端、qmy-admin 采购页面/API、采购状态、退货、通知、导出和跨组件边界。
- `SOL-50` 按验收要求保持 `status: draft`，明确采购申请和采购单虽有 qmy-java 参考证据，但正式 DDL、权限闭环、状态机和运行验收仍未完成，不能标记可直接复用。
- 更新 `wiki/COMP_采购供应商.md`、`wiki/SOURCE_INDEX.md`、`wiki/index.md`，将采购供应商组件纳入组件快照资产入口。
- 启动并完成 Multica `SOL-49` 本地组件抽取：生成 `COMPONENTS/order-management`，覆盖 `zhongsheng-AI` 基础订单 CRUD、qmy-java YT 复杂订单后端、qmy-admin 订单页面/API、订单状态、采购申请、退货、发货、完结、关闭和导出边界。
- `SOL-49` 快照明确基础 `orders/order_item` 与复杂 `sal_yt_order*` 差异，标注 qmy-java 缺完整 DDL、多个详情/退货/关闭/物流接口权限缺口，以及状态变更 GET 接口接入前需收口。
- `SOL-49` 严格复核后裁剪客户、组织、产品依赖 API，补入 `interface/table.ts`，并将 `footer-actions`、`product-selector`、`tagsStore` 标记为前端基座/共享依赖。
- 更新 `wiki/COMP_订单管理.md`、`wiki/SOURCE_INDEX.md`、`wiki/index.md`，将订单组件纳入组件快照资产入口。
- 启动并完成 Multica `SOL-48` 本地组件抽取：生成 `COMPONENTS/quote-management`，覆盖 `zhongsheng-AI` 基础报价 CRUD、qmy-java SED 复杂报价后端、qmy-admin 报价页面/API、成本明细、历史报价、审核、导出和转订单边界。
- `SOL-48` 快照明确基础 CRUD 与复杂报价流差异，标注 legacy `Quote.products` 与 `quote` SQL 不一致、qmy-java 缺完整 DDL、复杂流数据权限需接入前补齐。
- 更新 `wiki/COMP_报价管理.md`、`wiki/SOURCE_INDEX.md`、`wiki/index.md`，将报价组件纳入组件快照资产入口。

## 2026-05-01

- 记录新的组件整备方向：部分工厂客户可能更需要“工具箱”式轻量产品，而不是完整 ERP/CRM 一次性交付。
- 新增 `SYNTH_工厂工具箱组件化方向.md`，将产品选取生成 PI Excel、导出生产单、内部管理清单和模板化导出中心作为待验证场景。
- 更新 `wiki/index.md`，增加综合方向入口。
- 当前仅沉淀方向和依赖矩阵，未把工具箱场景标记为已实现组件。

## 2026-04-30

- 建立 ERP+CRM 组件知识库首版 `wiki/` 结构。
- 按 `RAW/` 只读、`wiki/` 编译、`pdoc/` 本地执行三层整理现有库。
- 生成第一批项目页：`PROJECT_qmy-admin`、`PROJECT_project-scaffold`、`PROJECT_zhongsheng-AI`、`PROJECT_zhongsheng-backend`、`PROJECT_zhongsheng调研资料`。
- 生成第一批业务组件卡：登录认证与权限、客户管理、报价管理、订单管理、产品物料基础数据、采购供应商、仓储发货、文件上传与 OSS。
- 更新 `wiki/SOURCE_INDEX.md` 并生成 `wiki/source_manifest.tsv`，当前 manifest 覆盖 2302 个来源文件，排除 `.DS_Store` 与嵌套 `.git/` 内部文件。
- 已知遗留：`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/jewelry-design` 本轮未深挖；Git 状态中已有 `.DS_Store` 暂存项，未直接回退用户已有暂存。
- 启动 Multica `SOL-41`，建立 `BASE/`、`COMPONENTS/`、`COMPONENTS/_TEMPLATE/` 快照目录规范。
- 新增组件快照模板：`README.md`、`component.yaml`、`SOURCE_MAP.md`、后端/前端/数据库占位说明、来源文档、整理规范、接口契约、数据契约、权限契约、验收清单。
- 启动并完成 Multica `SOL-42` 本地基座抽取：从 `RAW/PROJECTs/project-scaffold` 生成 `BASE/project-scaffold`，带出代码、README、SQL、原编码规范、基座规范、配置规范、API/数据/权限契约和验收清单。
- `SOL-42` 快照已排除源 `.git/`、`.DS_Store`、构建产物、环境配置文件，并在快照副本中移除硬编码调试超管 token 分支。
- 启动并完成 Multica `SOL-43` 本地组件抽取：生成 `COMPONENTS/auth-permission`，覆盖 `project-scaffold` 登录/JWT、`zhongsheng-backend` RBAC 权限、`qmy-admin` 前端登录/路由/按钮权限证据。
- `SOL-43` 快照未复制源环境配置、调试 token 与硬编码初始化管理员密码；组件状态标记为 `reference`。
- 启动并完成 Multica `SOL-44` 本地组件抽取：生成 `COMPONENTS/base-data`，覆盖 `project-scaffold` 通用基础数据、`zhongsheng-backend` 中圣基础数据、`qmy-admin` 字段管理页面和可编辑下拉组件证据。
- `SOL-44` 快照保留 `base_tree_node`、`base_data`、`nodeKey`、`biz_type`、权限码和字段管理契约；组件状态标记为 `reference`。
- 启动并完成 Multica `SOL-45` 本地组件抽取：生成 `COMPONENTS/file-oss`，覆盖 `zhongsheng-backend` OSS STS、`system_file` 文件记录、`qmy-admin` 上传组件和 `zhongsheng-AI` legacy 上传控制器对比。
- `SOL-45` 快照明确推荐 STS 临时凭证直传 OSS + `system_file` 文件记录，旧后端中转上传仅作为迁移参考；组件状态标记为 `reference`。
- 启动并完成 Multica `SOL-46` 本地组件抽取：生成 `COMPONENTS/product-material`，覆盖产品主档、产品 BOM、伞架、面料、材料、包材、工序、SQL、前端维护页、路由权限和契约文档。
- `SOL-46` 快照明确产品主档成本与报价时点快照的边界，并标注 `/fabric/deteil`、`/packaging/typeList`、`/box-price/list` 等待验证接口事实；组件状态标记为 `reference`。
- 启动并完成 Multica `SOL-47` 本地组件抽取：生成 `COMPONENTS/customer-management`，覆盖客户主档、联系人、地址、标签、跟进、客户数据权限、前端客户页面和调研材料。
- `SOL-47` 快照明确 legacy `Customer.owner/follower` 与 `init.sql` 缺列不一致，补充 `qmy-java` 作为 `/sal/yt/customer/*` 后端证据，并标注客户导入/平台同步不是默认能力；组件状态标记为 `reference`。
- `SOL-47` review 后补齐客户子资源写接口后端权限与客户范围校验，补入 `warehouse-history` 路由依赖、sed 客户 API 和可跟踪复核证据 `docs/acceptance/REVIEW_EVIDENCE.md`。
- `SOL-47` 二次复核后采纳有效项：删除接口从 GET 收口为 DELETE，并将客户组件路由裁剪到客户列表/新增/详情，移除订单、报价、独立仓历史等非最小客户档案入口。
