# REVIEW_EVIDENCE｜SOL-47 客户管理组件快照复核补齐

## 复核结论处理

本文件用于承接 `pdoc/report/REPORT_0430_SOL47客户管理组件快照.md` 的可跟踪证据。`pdoc/` 按项目规则仅保留在本机并写入 `.git/info/exclude`，因此组件可见产物统一放在 `COMPONENTS/customer-management/docs/acceptance/`。

## 阻塞项闭环

| 复核项 | 处理结果 | 证据 |
| :--- | :--- | :--- |
| P1 客户子资源写接口缺少后端数据范围闭环 | 已补齐核心客户写接口的 `@RequiresPermissions`、`@RequiresDataPermissions` 和客户范围校验 | `SalYtCustomerController.java`、`SalYtCustomerManager.java` |
| P2 组件快照缺少可跟踪报告 | 已新增本文件作为可跟踪复核证据；`pdoc/report/...` 保持本机经验报告 | `docs/acceptance/REVIEW_EVIDENCE.md` |
| P2 路由引用独立仓历史等非最小客户档案入口 | 已移除 `warehouse-history` 路由、页面和专用常量，避免组件快照继续耦合独立仓历史页面 | `frontend/qmy-admin/src/views/admin/router/async-modules/sales.ts`、`component.yaml` |
| sed 客户页面引用 sed 客户 API | 已补齐客户组件自身 API 文件 | `frontend/qmy-admin/src/api/sed/sales/customer.ts` |
| sales 路由保留非客户页面引用 | 已裁剪订单/报价/独立仓历史等非客户路由，避免组件快照引用未包含页面 | `frontend/qmy-admin/src/views/*/router/async-modules/sales.ts` |
| 破坏性操作仍使用 GET | 已将删除客户、地址、联系人、标签、跟进接口改为 DELETE，并将启用独立仓改为 POST，同步前端 API | `SalYtCustomerController.java`、`src/api/*/sales/customer.ts` |

## 后端权限与数据范围

客户主档和核心子资源写接口现在按同一口径处理：

| 能力 | 权限码 | 数据范围策略 |
| :--- | :--- | :--- |
| 客户详情 | `sal:yt:customer:detail` | 聚合查询前先校验客户主档范围，避免详情聚合逻辑关闭子查询数据权限后被绕过 |
| 修改客户主档 | `sal:yt:customer:update` | 校验目标客户在 `belong_employee_id OR follow_employee_id` 范围内 |
| 删除客户 | `sal:yt:customer:delete` | 先校验目标客户范围，再逻辑删除客户、地址、联系人 |
| 批量删除客户 | `sal:yt:customer:batchDelete` | 逐个校验客户范围 |
| 新增/编辑地址 | `sal:yt:customer:updateAddress` | 校验请求 `customerId` 对应客户范围 |
| 编辑已有地址 | `sal:yt:customer:updateAddress` | 解析地址真实所属客户，拒绝与请求 `customerId` 不一致的请求 |
| 删除地址 | `sal:yt:customer:address:delete` | 解析地址所属客户，校验客户范围，并拒绝与前端 `customerId` 不一致的请求 |
| 新增/编辑联系人 | `sal:yt:customer:updateContactPerson` | 校验请求 `customerId` 对应客户范围 |
| 编辑已有联系人 | `sal:yt:customer:updateContactPerson` | 解析联系人真实所属客户，拒绝与请求 `customerId` 不一致的请求 |
| 删除联系人 | `sal:yt:customer:contact:delete` | 解析联系人所属客户，校验客户范围，并拒绝与前端 `customerId` 不一致的请求 |
| 添加标签 | `sal:yt:customer:addLabel` | 校验 `masterId` 对应客户范围，再写客户标签 |
| 编辑既有标签 | `sal:yt:customer:addLabel` | 解析客户标签真实所属客户，拒绝与请求 `masterId` 不一致的请求 |
| 删除标签 | `sal:yt:customer:deleteLabel` | 解析客户标签所属客户，校验客户范围，并拒绝与前端 `customerId` 不一致的请求 |
| 新增/编辑跟进 | `sal:yt:customer:follow` | 校验请求 `customerId` 对应客户范围 |
| 编辑已有跟进 | `sal:yt:customer:follow` | 解析跟进记录真实所属客户，拒绝与请求 `customerId` 不一致的请求 |
| 删除跟进 | `sal:yt:customer:follow:delete` | 解析跟进记录所属客户，校验客户范围，并拒绝与前端 `customerId` 不一致的请求 |

## 最小化验收场景

| 场景 | 请求 | 预期 |
| :--- | :--- | :--- |
| 本人/部门范围用户编辑本人可见客户地址 | `POST /sal/yt/customer/createOrUpdateAddress`，body 带可见 `customerId` | 通过客户范围校验后保存 |
| 非范围用户直接调用删除地址 | `DELETE /sal/yt/customer/address/delete?addressId=...&customerId=...`，地址所属客户不可见 | `selectById(customerId)` 在数据权限下不可见，返回“客户不存在或无权限” |
| 用户伪造 `customerId` 删除他人标签 | `DELETE /sal/yt/customer/deleteLabel?labelId=...&customerId=其他客户` | 后端解析标签真实 `masterId`，发现与请求 `customerId` 不一致并拒绝 |
| 老板/全公司视角访问客户详情和子资源 | 用户具备全数据权限或租户未开启组织数据过滤 | 客户主档范围校验通过，子资源查询/写入可继续 |
| 报价/订单引用客户下拉 | `POST /sal/yt/customer/selectList` | 继续使用 `belong_employee_id OR follow_employee_id`，不能选到越权客户 |

## 静态验证记录

- 已扫描客户 Controller，核心客户写接口不再停留在注释权限状态。
- 已扫描前端路由引用，订单、报价、独立仓历史等非最小客户档案入口已裁剪。
- 已扫描前后端变更接口，客户、地址、联系人、标签、跟进删除均不再使用 GET，启用独立仓也不再使用 GET。
- 已扫描组件元数据，验收证据路径已写入 `component.yaml`。

说明：本次为组件快照复核补齐，未启动完整 qmy-java/qmy-admin 运行环境；正式接入项目后仍需用真实账号执行本人、部门、全公司、老板视角的接口回归。
