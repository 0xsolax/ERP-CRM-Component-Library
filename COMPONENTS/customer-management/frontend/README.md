# frontend

## 内容

| 来源 | 内容 |
| :--- | :--- |
| `src/views/admin/sales/customer` | 一唐客户列表、新增编辑、详情、地址、联系人、跟进、标签、独立仓、客户规格对照 |
| `src/views/sed/sales/customer` | 盛尔达客户列表、新增编辑、详情、地址、联系人、跟进、标签 |
| `src/api/admin/sales/customer.ts` | 客户主档、地址、联系人、标签、跟进、独立仓、客户层级、消费统计 API |
| `src/views/*/router/async-modules/sales.ts` | 客户路由和页面级权限码 |
| `src/constant/*/customer.ts` | 客户类型和手动层级枚举 |

## 接入判断

- 只做一个管理后台时，优先选 `admin/sales/customer` 或 `sed/sales/customer` 一套页面，不要同时接两套。
- `admin` 版本包含更多独立仓和客户产品规格对照能力；`sed` 版本更偏客户档案和销售跟进。
- 页面依赖全局组件和基础能力：`bz-table`、`footer-actions`、`dynamic.show`、`v-permission`、`tagsStore`、员工列表、国家地区接口、脱敏工具。

## 权限提示

- 页面级权限：`sal:yt:customer:list`、`sal:yt:customer:save`、`sal:yt:customer:detail`。
- 按钮级权限：`sal:yt:customer:update`、`delete`、`updateAddress`、`updateContactPerson`、`enableStore`、`auditStore`、`setAutoLevel` 等。
- 前端权限只用于展示控制，正式安全边界必须在后端接口实现。

