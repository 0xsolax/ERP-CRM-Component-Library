# backend

## 内容

| 来源 | 内容 | 复用定位 |
| :--- | :--- | :--- |
| `zhongsheng-AI` | 简版客户 CRUD、`customer` 表实体和分页过滤 | legacy 参考，不建议直接接入 |
| `qmy-java` | 客户主档、联系人、地址、标签、跟进、独立仓、数据权限 | 主要参考来源 |

## 接入判断

- 最小客户档案：参考 `zhongsheng-AI` 的 controller/service 分层，但必须补齐 `owner`、`follower` schema 或删除相关过滤。
- 完整 CRM 客户管理：参考 `qmy-java` 的 `SalYtCustomerController`、`SalYtCustomerManager` 和 Mapper XML。
- 权限与数据范围：不要只复制客户 controller，必须同时接入 `auth-permission`、`RequiresDataPermissions` 和 SQL 拦截器能力。

## 已知风险

- `qmy-java` 是历史项目快照，不是本组件独立可编译后端。
- `qmy-java` 中部分地址、联系人、标签、跟进、独立仓接口的 `@RequiresPermissions` 被注释，正式接入前要按 `PERMISSION_CONTRACT.md` 补齐。
- 客户标签当前复用产品标签表，跨组件接入时需要确认是否保持复用。

