# REVIEW_EVIDENCE｜SOL-50 purchase-supplier

## 本次执行范围

- 创建 `COMPONENTS/purchase-supplier` 标准组件快照。
- 复制 `zhongsheng-AI` 供应商 CRUD 后端、`supplier` SQL 和 PRD。
- 复制 qmy-java YT 采购复杂流后端证据：供应商、待采购、采购单、库存预警、采购跟进、退货、导出。
- 复制 qmy-admin admin 采购页面、采购 API、采购路由、采购常量，并裁剪直接依赖 API。
- 编写 README、SOURCE_MAP、组件规范、API/数据/权限契约和验收清单。
- 更新 wiki 组件卡、组件索引、本地报告和记忆日志。

## 关键来源核验

| 核验项 | 结果 |
| :--- | :--- |
| Multica issue | `SOL-50`，待验证业务组件快照：`purchase-supplier` |
| 供应商 CRUD 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/SupplierController.java` |
| 供应商 SQL | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` 中 `supplier` |
| 复杂供应商后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtSupplierController.java` |
| 待采购后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java` |
| 采购单后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtPurchaseController.java` |
| 库存预警后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtStoreWarningController.java` |
| 前端页面 | `RAW/PROJECTs/qmy-admin/src/views/admin/purchase` |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/admin/purchase` |
| 调研材料 | `RAW/docs/zhongsheng` |

## 已执行命令证据

```text
multica issue get SOL-50 --output json
multica issue comment list SOL-50 --limit 30 --output json
multica issue runs SOL-50 --output json
multica issue status SOL-50 in_progress --output json
find RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp -type f | rg 'Supplier|Purchase|supplier|purchase'
find RAW/PROJECTs/qmy-admin/src/views/admin/purchase -maxdepth 5 -type f
find RAW/PROJECTs/qmy-admin/src/api/admin/purchase -maxdepth 5 -type f
find RAW/PROJECTs/qmy-java -type f | rg 'PurYt|Supplier|Purchase|supplier|purchase'
rg -n '@((Get|Post|Delete|Put)Mapping|RequiresPermissions|RequiresDataPermissions)' RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt
```

## 复核结论

- 供应商基础 CRUD 可追溯到 `zhongsheng-AI` 后端与 `supplier` SQL。
- qmy-admin 采购页面实际存在 qmy-java 对应后端，已作为补充证据纳入。
- 组件仍保持 `draft`，因为正式采购 DDL、状态机、权限闭环和运行验收未完成。
- 报价阶段不强绑定供应商、采购阶段按供应商聚合采购单的边界已写入规范。
- 仓储入库、库存流水、财务付款和供应商对账已明确为跨组件边界。

## Review 自查修正

- 自查发现采购页面直接导入 `footer-actions`、`product-selector`、tags/permission store、download/auth/validate/utils 等 qmy-admin 共享依赖。
- 这些共享文件未复制进草稿组件，已在 `component.yaml`、`SOURCE_MAP.md`、`frontend/README.md` 和 `wiki/SOURCE_INDEX.md` 标为基座/共享依赖，避免装配时误判为组件内文件。
- Review 修正：`product/index.ts`、`customer.ts`、`org.ts` 从原始整文件复制改为只保留采购页面实际调用的 `getSupplierSelect`、`getCustomerList`、`getAllEmployee`。
- Review 修正：`order-management`、`product-material` 从必选依赖改为复杂采购流建议依赖；简版供应商 CRUD 只要求 P0 基座与认证权限。

## 静态验证记录

| 验证项 | 结果 |
| :--- | :--- |
| 快照文件数量 | 116 个文件 |
| 污染文件扫描 | 未发现 `.DS_Store`、`.git`、`node_modules`、`target`、`dist`、`build` |
| 精确敏感配置扫描 | 未发现数据库连接串、云访问密钥、私钥或口令赋值 |
| 宽松 token 扫描 | 仅命中文档排除项 `secrets`、前端说明文字和请求头名 `qiaomoyun-token`，未发现真实密钥 |
| 采购路由扫描 | 未发现销售订单、报价、仓库或财务页面路由引用 |
| 关键文档 | `README.md`、`SOURCE_MAP.md`、`COMPONENT_SPEC.md`、`API_CONTRACT.md`、`DATA_CONTRACT.md`、`PERMISSION_CONTRACT.md`、`ACCEPTANCE.md` 均存在 |
| 行尾空白扫描 | 本次范围无输出 |
| 已跟踪文件 diff 检查 | `git diff --check` 对已跟踪修改无输出 |

说明：本次为组件快照抽取与契约整理，未启动 qmy-java/qmy-admin 完整运行环境；正式接入项目后仍需真实账号和真实数据库做接口级回归。
