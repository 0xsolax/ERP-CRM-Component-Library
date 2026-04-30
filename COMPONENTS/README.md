# COMPONENTS 组件快照层

## 定位

`COMPONENTS/` 存放可装配的 ERP/CRM 组件快照。每个组件都应是一个可独立理解、可追溯来源、可按文档人工接入新项目的资产包。

组件不是单个 UI 控件，也不是只复制后端代码。组件必须包含业务目标、来源、代码快照、接口契约、数据结构、权限边界、验收清单和已知风险。

## 标准组件结构

```text
COMPONENTS/<component-name>/
  component.yaml
  README.md
  SOURCE_MAP.md
  backend/
  frontend/
  db/
  docs/
    source/
    spec/
    contracts/
    acceptance/
```

## 目录职责

| 目录 | 说明 |
| :--- | :--- |
| `backend/` | 后端 controller/service/DTO/VO/domain/manager/dao 等快照 |
| `frontend/` | 前端 views/api/router/store/components 等快照 |
| `db/` | 表结构、初始化数据、迁移片段、字段说明 |
| `docs/source/` | 原项目带出的 README、PRD、调研记录、接口说明、规范文档 |
| `docs/spec/` | 组件库整理后的业务和实现规范 |
| `docs/contracts/` | API、数据、权限、状态、错误码契约 |
| `docs/acceptance/` | 组件验收清单、装配检查、风险检查 |

## 状态定义

| 状态 | 说明 |
| :--- | :--- |
| `ready` | 代码、文档、契约、验收都完整，可作为新项目优先参考 |
| `reference` | 证据较完整，但需要按项目改造后使用 |
| `draft` | 来源不足或后端/前端/数据缺口明显，只能作为待验证草稿 |

## 第一批组件计划

- [auth-permission](auth-permission/README.md) - `reference`，已抽取登录、JWT、Token、用户、角色、菜单、权限和前端守卫快照
- [base-data](base-data/README.md) - `reference`，已抽取基础数据树、数据行、字段管理、nodeKey、权限码、SQL 和前端字段维护快照
- [file-oss](file-oss/README.md) - `reference`，已抽取 OSS STS、文件记录、上传组件、system_file SQL 和旧上传控制器对比快照
- [product-material](product-material/README.md) - `reference`，已抽取产品主档、产品 BOM、伞架、面料、材料、包材、工序、SQL、前端维护页和权限契约
- [customer-management](customer-management/README.md) - `reference`，已抽取客户主档、联系人、地址、标签、跟进、客户数据权限、前端页面和导入/同步边界
- `quote-management`
- `order-management`
- `purchase-supplier`
- `warehouse-delivery`

## 使用顺序

1. 先读组件 `README.md` 判断是否适配当前项目。
2. 再读 `SOURCE_MAP.md` 确认来源和缺口。
3. 对照 `docs/contracts/` 确认接口、数据和权限。
4. 复制或改造 `backend/`、`frontend/`、`db/`。
5. 按 `docs/acceptance/ACCEPTANCE.md` 验收。
