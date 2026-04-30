# 维护日志

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
