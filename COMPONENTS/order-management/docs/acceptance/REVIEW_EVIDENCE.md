# REVIEW_EVIDENCE｜SOL-49 order-management

## 本次执行范围

- 创建 `COMPONENTS/order-management` 标准组件快照。
- 复制 `zhongsheng-AI` 基础订单 CRUD 后端、`orders/order_item` SQL 和 PRD。
- 复制 qmy-java YT 复杂订单后端证据：订单、子订单、商品项、退货、半成品、发货、关闭、导出、采购申请。
- 复制 qmy-admin admin 订单页面、订单 API、订单常量和裁剪后的订单路由。
- 复核后补入订单列表直接使用的 `interface/table.ts`，并把 `footer-actions`、`product-selector`、`tagsStore` 等递归共享依赖明确为装配前置，而不是订单私有能力。
- 复核后裁剪客户、组织、产品依赖 API，只保留订单页面实际调用函数，避免把客户维护、组织账号、产品维护误收为订单组件能力。
- 编写 README、SOURCE_MAP、组件规范、API/数据/权限契约和验收清单。
- 更新 wiki 组件卡、组件索引、本地报告和记忆日志。

## 关键来源核验

| 核验项 | 结果 |
| :--- | :--- |
| Multica issue | `SOL-49`，业务组件快照：`order-management` |
| 基础 CRUD 后端 | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/OrdersController.java` |
| 基础 CRUD SQL | `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql` 中 `orders`、`order_item` |
| 复杂订单后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/sal/yt/SalYtOrderController.java` |
| 采购申请边界后端 | `RAW/PROJECTs/qmy-java/web/src/main/java/com/qiaomoyun/controller/pur/yt/PurYtApplyPurchaseController.java` |
| 复杂订单前端 | `RAW/PROJECTs/qmy-admin/src/views/admin/sales/order` |
| 前端 API | `RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts` |
| 前端类型 | `RAW/PROJECTs/qmy-admin/src/interface/table.ts` |
| 前端共享依赖 | `RAW/PROJECTs/qmy-admin/src/components/product-selector`、`RAW/PROJECTs/qmy-admin/src/components/footer-actions/index.vue`、`RAW/PROJECTs/qmy-admin/src/views/admin/store/modules/tags/index.ts` |
| 调研材料 | `RAW/docs/zhongsheng` |

## 已执行命令证据

```text
multica issue get SOL-49 --output json
multica issue comment list SOL-49 --output json
multica issue runs SOL-49 --output json
multica issue status SOL-49 in_progress --output json
find RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp -type f | rg 'Order|Orders|order'
find RAW/PROJECTs/qmy-admin/src/views/admin/sales/order -maxdepth 5 -type f
rg -n "/sal/yt/order|/pur/yt/applyPurchase" RAW/PROJECTs/qmy-admin/src/api/admin/sales/order.ts
find RAW/PROJECTs/qmy-java -type f | rg "SalYtOrder|SalYtReturnOrder|PurYtApplyPurchase|OrderSubItem|DeliveryOrderStatus|ShippingMethod"
rg -n "@((Get|Post|Delete|Put)Mapping|RequiresPermissions|RequiresDataPermissions)" COMPONENTS/order-management/backend/qmy-java/web/src/main/java/com/qiaomoyun/controller
```

## 复核结论

- 基础订单 CRUD 与 YT 复杂订单流已分层，不再把 `OrdersController` 误判为完整订单能力。
- 订单主表、明细、状态、来源报价、采购申请、退货、完结、关闭、仓储/财务边界均已写入契约。
- 采购申请、仓储发货、财务回款已明确为跨组件边界，不并入订单组件默认能力。
- qmy-java 复杂流缺完整 DDL，已在数据契约和验收清单中标为接入前必须补齐。
- qmy-java 多个详情/退货/关闭/物流接口权限为空或注释，且存在状态变更 GET，已在权限契约和验收清单中标为接入风险。

## 静态验证记录

| 验证项 | 结果 |
| :--- | :--- |
| 快照文件数量 | 104 个文件 |
| 污染文件扫描 | 组件快照范围内未发现 `.DS_Store`、`.git`、`node_modules`、`target`、`dist`、`build` |
| 精确敏感配置扫描 | 未发现数据库连接串、云访问密钥、私钥或口令赋值 |
| 宽松 token 扫描 | 仅命中文档排除项 `secrets` 和前端请求头名 `qiaomoyun-token`，未发现真实密钥 |
| 订单路由裁剪扫描 | 未发现 `sales/customer`、`warehouse-history`、客户页面、仓库历史或报价路由引用 |
| 关键文档 | `README.md`、`SOURCE_MAP.md`、`COMPONENT_SPEC.md`、`API_CONTRACT.md`、`DATA_CONTRACT.md`、`PERMISSION_CONTRACT.md`、`ACCEPTANCE.md` 均存在 |
| 来源 TODO 扫描 | 仅命中 qmy-java 历史源码原注释，作为来源事实保留，未新增待办占位 |
| 依赖 API 裁剪 | `customer.ts` 仅保留 2 个订单函数，`org.ts` 仅保留 1 个订单函数，`product/index.ts` 仅保留 2 个订单函数 |
| 依赖 API 路径复核 | `getCategoryLabelList` 已对齐 RAW 前端和 qmy-java 后端的 `/pro/yt/product/categoryLabelList` |
| 行尾空白扫描 | 本次范围无输出 |
| 已跟踪文件 diff 检查 | `git diff --check` 对已跟踪修改无输出 |

说明：本次为组件快照抽取与契约整理，未启动 qmy-java/qmy-admin 完整运行环境；正式接入项目后仍需真实账号和真实数据库做接口级回归。
