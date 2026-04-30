# BASE 基座快照层

## 定位

`BASE/` 存放可被新 ERP/CRM 项目继承的项目基座快照。基座不是原始项目仓库，也不是开发中的业务项目；它是从 `RAW/PROJECTs/*` 抽取、清洗、说明后的可复用母版。

第一版基座来源：

- `RAW/PROJECTs/project-scaffold`

## 基座快照必须包含

| 目录 | 说明 |
| :--- | :--- |
| `code/` | 可复制的基座代码快照，不能包含嵌套 `.git/`、构建产物或本地配置 |
| `docs/source/` | 从来源项目带出的原始 README、SQL 说明、编码规范等原文快照 |
| `docs/spec/` | 组件库重新整理后的基座开发规范 |
| `docs/contracts/` | API、数据表、权限、错误码、配置项契约 |
| `docs/acceptance/` | 基座验收清单 |
| `SOURCE_MAP.md` | 来源路径、抽取范围、未抽取内容、清洗规则 |
| `README.md` | 基座用途、接入步骤、依赖和验收入口 |

## 快照规则

- `RAW/` 是证据层，`BASE/` 是可复用资产层。
- 从 `RAW/PROJECTs/project-scaffold` 抽取时必须去除 `.git/`、`.DS_Store`、`target/`、本地密钥和机器相关配置。
- 原项目文档要进入 `docs/source/`，不能只复制代码。
- 重新整理后的规范进入 `docs/spec/` 和 `docs/contracts/`。
- 如果来源文档和代码存在冲突，必须在 `SOURCE_MAP.md` 或 `docs/spec/` 中标记为待验证。

## 当前计划

- [project-scaffold](project-scaffold/README.md)：后端 Spring Boot 多模块基座，来自 `RAW/PROJECTs/project-scaffold`。

## 当前快照状态

| 基座 | 状态 | 说明 |
| :--- | :--- | :--- |
| `project-scaffold` | `reference` | 已抽取代码、原始 README、SQL、原编码规范，并补齐基座规范、配置规范、API/数据/权限契约和验收清单 |
