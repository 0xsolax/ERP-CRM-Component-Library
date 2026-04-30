# 维护日志

## 2026-04-30

- 建立 ERP+CRM 组件知识库首版 `wiki/` 结构。
- 按 `RAW/` 只读、`wiki/` 编译、`pdoc/` 本地执行三层整理现有库。
- 生成第一批项目页：`PROJECT_qmy-admin`、`PROJECT_project-scaffold`、`PROJECT_zhongsheng-AI`、`PROJECT_zhongsheng-backend`、`PROJECT_zhongsheng调研资料`。
- 生成第一批业务组件卡：登录认证与权限、客户管理、报价管理、订单管理、产品物料基础数据、采购供应商、仓储发货、文件上传与 OSS。
- 更新 `wiki/SOURCE_INDEX.md` 并生成 `wiki/source_manifest.tsv`，当前 manifest 覆盖 2302 个来源文件，排除 `.DS_Store` 与嵌套 `.git/` 内部文件。
- 已知遗留：`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/jewelry-design` 本轮未深挖；Git 状态中已有 `.DS_Store` 暂存项，未直接回退用户已有暂存。
