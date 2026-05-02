# REVIEW_EVIDENCE｜SOL-51 warehouse-delivery

## 本次执行范围

- 创建 `COMPONENTS/warehouse-delivery` 标准组件快照。
- 复制 qmy-admin 仓储页面、legacy 发货页面、仓储 API、legacy 发货 API、仓储/发货路由和直接依赖常量/API。
- 复制 qmy-java `sto/yt` 仓储发货后端证据：controller、manager、entity、param、VO、mapper、事件、监听器、库存预警任务、枚举和导出模板。
- 编写 README、SOURCE_MAP、组件规范、API/数据/权限契约和验收清单。
- 更新 wiki 组件卡、组件索引、本地报告和记忆日志。

## 关键来源核验

| 核验项 | 结果 |
| :--- | :--- |
| Multica issue | `SOL-51`，待验证业务组件快照：`warehouse-delivery` |
| 前端仓储页面 | `RAW/PROJECTs/qmy-admin/src/views/admin/warehouse` |
| 前端 legacy 发货页面 | `RAW/PROJECTs/qmy-admin/src/views/admin/delivery` |
| 前端仓储 API | `RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts` |
| 前端 legacy 发货 API | `RAW/PROJECTs/qmy-admin/src/api/admin/delivery/index.ts` |
| 启用仓储路由 | `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/warehouse.ts` |
| 停用发货路由 | `RAW/PROJECTs/qmy-admin/src/views/admin/router/async-modules/delivery.ts`，整体注释 |
| 后端 controller | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sto/yt` |
| 后端 manager | `RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sto/yt` |
| 后端实体/mapper | `RAW/PROJECTs/qmy-java/entity/src/main/java/com/qiaomoyun/entity/sto/yt`、`dao/.../sto/yt` |
| 调研材料 | `RAW/docs/zhongsheng` |

## 已执行命令证据

```text
multica issue get SOL-51 --output json
multica issue comment list SOL-51 --limit 30 --output json
multica issue runs SOL-51 --output json
multica issue status SOL-51 in_progress --output json
find RAW/PROJECTs/qmy-admin/src/views/admin/warehouse -maxdepth 5 -type f
find RAW/PROJECTs/qmy-admin/src/views/admin/delivery -maxdepth 5 -type f
rg -n "export const" RAW/PROJECTs/qmy-admin/src/api/admin/warehouse/index.ts RAW/PROJECTs/qmy-admin/src/api/admin/delivery/index.ts
find RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sto/yt -maxdepth 1 -type f
find RAW/PROJECTs/qmy-java/service/src/main/java/com/qiaomoyun/manager/sto/yt -maxdepth 1 -type f
rg -n '@((Get|Post|Put|Delete)Mapping|RequiresPermissions|RequiresDataPermissions)' RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sto/yt
find COMPONENTS/warehouse-delivery -type f | wc -l
find COMPONENTS/warehouse-delivery \( -name .DS_Store -o -name .git -o -name node_modules -o -name target -o -name dist -o -name build \) -print
精确敏感配置扫描命令：扫描数据库连接串、云访问密钥、私钥和口令赋值
rg -n "sales/order|quotation|views/admin/sales/order|views/admin/purchase|views/admin/finance" COMPONENTS/warehouse-delivery/frontend/qmy-admin/src/views/admin/router/async-modules/warehouse.ts COMPONENTS/warehouse-delivery/frontend/qmy-admin/src/views/admin/router/async-modules/delivery.ts
git diff --check -- COMPONENTS/README.md wiki/COMP_仓储发货.md wiki/SOURCE_INDEX.md wiki/index.md wiki/log.md
git diff --check -- COMPONENTS/warehouse-delivery
multica issue status SOL-51 in_review --output json
```

## 复核结论

- SOL-51 要求的前端 API 能力清单已整理。
- 后端 controller/manager/entity/mapper 证据已找到并带出，问题不是“无后端”，而是“缺正式 DDL、权限闭环和运行验收”。
- 组件保持 `draft`，符合“未确认库存扣减规则前不得标记为可直接复用”的要求。
- qmy-admin `delivery` 路由当前整体注释，默认接入不应启用 legacy 发货入口。
- qmy-java 中仓储发货接口存在权限注释、缺数据范围和破坏性 GET，已写入权限契约和接入前清单。

## 静态验证记录

| 验证项 | 结果 |
| :--- | :--- |
| 快照文件数量 | 136 个文件 |
| 污染文件扫描 | 未发现 `.DS_Store`、`.git`、`node_modules`、`target`、`dist`、`build` |
| 精确敏感配置扫描 | 未发现数据库连接串、云访问密钥、私钥或口令赋值 |
| 宽松敏感词扫描 | 仅命中 `component.yaml` 的敏感文件排除项名称，未发现真实密钥 |
| 路由范围扫描 | 仓储/发货路由未引用订单、报价、采购或财务页面 |
| 关键文档 | `README.md`、`SOURCE_MAP.md`、`COMPONENT_SPEC.md`、`API_CONTRACT.md`、`DATA_CONTRACT.md`、`PERMISSION_CONTRACT.md`、`ACCEPTANCE.md` 均存在 |
| 行尾空白扫描 | 本次范围无输出 |
| 已跟踪文件 diff 检查 | `git diff --check` 对已跟踪修改无输出 |

说明：本次为组件快照抽取与契约整理，未启动 qmy-java/qmy-admin 完整运行环境；正式接入项目后仍需真实账号和真实数据库做接口级回归。
