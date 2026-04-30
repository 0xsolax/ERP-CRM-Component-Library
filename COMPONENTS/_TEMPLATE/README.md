# 组件名称

## 定位

说明该组件解决什么业务问题、服务哪些角色、处于 ERP/CRM 哪条流程中。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `draft` / `reference` / `ready` |
| 组件类型 | 基础组件 / 业务组件 / 待验证组件 |
| 依赖组件 | 例如 `auth-permission`、`base-data`、`file-oss` |
| 主要来源 | `RAW/...` |

## 快速接入

1. 阅读 `SOURCE_MAP.md`，确认来源和缺口。
2. 阅读 `docs/contracts/`，确认 API、数据表、权限、状态流。
3. 按目标项目技术栈改造 `backend/`、`frontend/`、`db/`。
4. 按 `docs/acceptance/ACCEPTANCE.md` 验收。

## 目录说明

| 目录 | 内容 |
| :--- | :--- |
| `backend/` | 后端代码快照 |
| `frontend/` | 前端代码快照 |
| `db/` | 表结构、初始化和迁移片段 |
| `docs/source/` | 原始规范、PRD、README、会议记录等来源快照 |
| `docs/spec/` | 组件库整理后的规范 |
| `docs/contracts/` | API、数据、权限、状态契约 |
| `docs/acceptance/` | 验收清单 |

## 禁止项

- 不提交 `.git/`、`.DS_Store`、`target/`、`node_modules/`。
- 不提交本地密钥、Token、数据库密码、OSS AccessKey。
- 不把 PRD 或会议记录中的需求直接写成已实现能力。
- 不把单客户特殊逻辑默认当成通用组件逻辑。

