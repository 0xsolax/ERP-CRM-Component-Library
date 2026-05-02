# REVIEW_EVIDENCE｜SOL-48 quote-management

## 本次执行范围

- 创建 `COMPONENTS/quote-management` 标准组件快照。
- 复制 `zhongsheng-AI` 基础报价 CRUD 后端、PRD 和 legacy SQL。
- 复制 qmy-java SED 报价复杂流后端证据。
- 复制 qmy-admin SED 报价页面、API、常量、表格类型和路由入口。
- 编写 README、SOURCE_MAP、组件规范、API/数据/权限契约和验收清单。
- 更新 wiki 组件卡、组件索引、本地报告和记忆日志。

## 关键来源核验

| 核验项 | 结果 |
| :--- | :--- |
| Multica issue | `SOL-48`，业务组件快照：`quote-management` |
| 基础 CRUD 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/QuoteController.java` |
| 基础 CRUD SQL | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` 中 `quote`、`quote_item` |
| 复杂流后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/sed/SalSedQuotationController.java` |
| 复杂流前端 | `RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation` |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/sed/sales/quotation.ts` |
| 调研材料 | `RAW/docs/zhongsheng` |

## 已执行命令证据

```text
multica issue get SOL-48 --output json
multica issue comment list SOL-48 --output json
multica issue runs SOL-48 --output json
multica issue status SOL-48 in_progress --output json
find RAW/PROJECTs/qmy-admin/src/views/sed/sales/quotation -maxdepth 5 -type f
rg -n "sal/sed/quotation|SedQuotation|quotationDetail|presidentWxAudit|mergeToOrder|oneKeyToOrder" RAW/PROJECTs/qmy-java RAW/PROJECTs/zhongsheng-AI
find RAW/PROJECTs/qmy-java -type f | rg "SalSedQuotation|mapper/sal/sed/SalSedQuotation|QuotationOperation|sal/sed/SalSedQuotation|sed/quotation"
rg -n "CREATE TABLE.*sal_sed_quotation|sal_sed_quotation\\s*\\(" RAW/PROJECTs/qmy-java RAW/PROJECTs -g "*.sql"
```

## 复核结论

- 基础 CRUD 与复杂报价流已分层，不再把 `QuoteController` 误判为完整报价能力。
- 成本明细、历史报价、审核、转订单均能追溯到 qmy-java controller/service/manager 与 qmy-admin 页面/API。
- 客户敏感信息、成本、毛利率、历史报价和导出风险已写入权限契约。
- qmy-java 复杂流没有完整 DDL，已在数据契约和验收中标为接入风险。
- qmy-java 来源多数按 ID 的详情/写接口缺少数据范围守卫，已在权限契约中标为接入前必须补齐。

## 静态验证

| 验证项 | 结果 |
| :--- | :--- |
| 快照文件数量 | 120 个文件 |
| 污染文件扫描 | 未发现 `.DS_Store`、`.git`、`node_modules`、`target`、`dist`、`build` |
| 精确敏感配置扫描 | 未发现数据库连接串、云访问密钥、私钥或口令赋值 |
| 宽松 token 扫描 | 仅命中文档排除项 `secrets` 和前端请求头名 `qiaomoyun-token`，未发现真实密钥 |
| 关键文档 | `README.md`、`SOURCE_MAP.md`、`COMPONENT_SPEC.md`、`API_CONTRACT.md`、`DATA_CONTRACT.md`、`PERMISSION_CONTRACT.md`、`ACCEPTANCE.md` 均存在 |
| 行尾空白扫描 | 本次范围无输出 |
| 已跟踪文件 diff 检查 | `git diff --check` 对已跟踪修改无输出 |

## Review 修正

- 复核发现报价页直接导入 `@/interface/table`，原快照未复制该类型文件。
- 已补入 `frontend/qmy-admin/src/interface/table.ts`，并同步 `component.yaml`、`SOURCE_MAP.md`、`frontend/README.md` 和 `ACCEPTANCE.md`。
- 复核发现客户/包材依赖 API 原样复制过宽，已裁剪为报价页实际使用函数，避免带入客户删除、独立仓、完整包材维护等非报价入口。
- 子代理复核发现 qmy-java 后端 `/getUserInfo` 未进入 API/权限契约；已补入 `API_CONTRACT.md`、`PERMISSION_CONTRACT.md`、`README.md` 和 `wiki/COMP_报价管理.md`，并标明当前前端未直接封装但仍属于客户敏感信息边界。
- 子代理复核发现 `wiki/SOURCE_INDEX.md` 未同步 `file-type.ts`、`interface/table.ts`、报价路由和裁剪后的依赖 API 来源；已补齐。
- 子代理关于 `interface/table.ts` untracked 的提醒有效但不在本轮执行 `git add`；当前需在提交前将 `COMPONENTS/quote-management/frontend/qmy-admin/src/interface/table.ts` 一并纳入。
