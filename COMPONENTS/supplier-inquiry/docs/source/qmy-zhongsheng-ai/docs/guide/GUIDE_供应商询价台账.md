# GUIDE_供应商询价台账

| 字段 | 内容 |
| :--- | :--- |
| 文档类型 | GUIDE |
| 版本号 | v1.5.3 |
| 创建日期 | 2026-05-06 |
| 负责人 | Solazhu |
| 适用范围 | 中圣供应商询价历史、采购前价格台账、供应商/材料/产品维度筛选 |

---

## 1. 功能定位

供应商询价台账用于记录采购前向供应商询价的历史价格，服务于后续查询、筛选和人工比价判断。

本模块只记录询价事实：

- 不自动生成采购单。
- 不自动改写材料、面料、包材、伞架或产品主档价格。
- 不进入库存、入库、付款或采购审批链路。
- 正式比价单、采用报价到采购单、供应商评分和附件识别作为后续 Backlog。

## 2. 菜单入口

```text
采购管理
  - 采购单列表
  - 供应商询价
  - 供应商列表
```

页面路由：

```text
/supplier/inquiry
```

放在采购管理下，是因为该台账发生在采购前，与供应商列表、采购单列表关系最近；它不属于销售报价，也不直接挂在生产管理下，避免客户报价历史、生产派单和采购询价三类语义混淆。

## 3. 前端入口

| 文件 | 说明 |
| :--- | :--- |
| `zhongsheng-admin/src/views/zs/supplier-inquiry/index.vue` | 供应商询价台账页面，包含筛选、列表、新增、详情内编辑、供应商/询价对象历史抽屉和编辑日志展示 |
| `zhongsheng-admin/src/views/zs/supplier/index.vue` | 供应商列表中的 `历史` 抽屉可只读查看该供应商的询价历史 |
| `zhongsheng-admin/src/api/zs/supplier-inquiry/index.ts` | 供应商询价 API 封装 |
| `zhongsheng-admin/src/views/zs/router/async-modules/purchase.ts` | `采购管理 / 供应商询价` 路由入口 |
| `zhongsheng-admin/src/views/zs/constants/permissions.ts` | 前端权限常量 |
| `zhongsheng-admin/src/views/zs/system/role/permission-display.ts` | 角色授权矩阵展示归类 |

页面第一版能力：

- 按关键词、供应商、对象类型、币种、报价日期、有效状态筛选。
- 新增、查看、删除询价记录；编辑入口收口到详情弹窗右下角，不在列表操作栏直接暴露。
- 支持从供应商主档下拉选择供应商；供应商主档为空时，可在询价弹窗内快捷新增供应商并立即选中。
- 对象类型支持预置值和快捷新增，新增后只进入当前页面选项，不自动生成主档字典。
- 支持从材料、面料、包材、伞架、产品主档搜索选择询价对象。
- 询价对象支持快捷新增手工对象，便于数据不完整时先记录询价。
- 供应商名称可点击打开该供应商全部询价历史。
- 供应商列表页也提供 `历史` 按钮，方便从供应商维度查看询价历史；该入口只读，不在供应商主档内编辑询价记录。
- 询价对象名称可点击打开该对象全部历史报价；主档对象按对象 ID 查询，手工对象按对象类型和对象名称关键词查询。
- 列表与历史抽屉中的单价按 2 位小数展示；起订量默认去掉无效小数位，整数起订量显示为整数。
- 编辑保存时后端自动记录字段级修改日志，详情弹窗中展示日志；新增和无实际字段变化的保存不生成修改日志。
- 筛选区使用响应式网格排布，关键词、供应商、对象类型、币种、报价日期、有效状态和操作按钮按可用宽度自动重排，避免大屏空白浪费和控件被挤压。 - v1.5.1
- 列表列顺序为报价日期、供应商、询价对象、单价/单位、起订量、交期、有效期、税率、联系人、录入/更新、操作；单价与单位合并展示，减少横向列数。 - v1.5.2
- 供应商快捷新增成功后全量刷新供应商下拉，并保留新增项兜底；不得用新增名称作为关键词替换当前下拉选项列表。 - v1.5.3

## 4. 后端入口

| 文件 | 说明 |
| :--- | :--- |
| `SupplierInquiryController` | `/supplier/inquiry/*` 接口 |
| `SupplierInquiryServiceImpl` | 校验、供应商快照、录入人快照、保存、编辑日志与删除 |
| `SupplierInquiryManagerImpl` | MyBatis-Plus 分页、筛选、软删除 |
| `SupplierInquiryDO` | `supplier_inquiry` 表映射 |
| `SupplierInquirySaveDTO`、`SupplierInquiryListQueryDTO`、`SupplierInquiryHistoryQueryDTO` | 保存、分页和历史查询请求模型 |
| `SupplierInquiryVO` | 询价台账返回模型 |

## 5. 接口与权限

| 接口 | 权限 | 说明 |
| :--- | :--- | :--- |
| `POST /supplier/inquiry/page` | `supplier:inquiry:page` | 分页查询供应商询价台账 |
| `POST /supplier/inquiry/history` | `supplier:inquiry:page` | 查询供应商或询价对象历史 |
| `POST /supplier/inquiry/detail` | `supplier:inquiry:detail` | 查询询价详情 |
| `POST /supplier/inquiry/saveOrUpdate` | `supplier:inquiry:save` | 新增或编辑询价记录 |
| `POST /supplier/inquiry/delete` | `supplier:inquiry:remove` | 逻辑删除询价记录 |

权限常量由 `ApiPermissionConstants` 维护，启动时由 `ApiPermissionMenuBootstrap` 补齐接口权限占位；页面级菜单由 `ZsBusinessMenuBootstrap` 同步。

## 6. 数据表

SQL 初始化脚本：

```text
zhongsheng-backend/docs/sql/init-supplier-inquiry.sql
```

后端应用启动时由 `SupplierInquirySchemaInitializer` 执行 `CREATE TABLE IF NOT EXISTS supplier_inquiry`，用于避免本地或测试库漏执行 SQL 后进入页面弹出“系统异常”；SQL 文件仍作为表结构的人工核对来源。

数据表：

```text
supplier_inquiry
```

核心字段：

- `supplier_id`、`supplier_code`、`supplier_name`：供应商主档与快照。
- `target_type`、`target_id`、`target_code`、`target_name`：询价对象类型、关联 ID 与快照。
- `specification`、`unit`：规格与单位快照。
- `price`、`currency`、`tax_rate`、`moq`、`delivery_days`：询价核心条件。
- `quote_date`、`valid_until`：报价日期与有效期。
- `contact_name`、`contact_phone`：本次询价联系人快照。
- `owner_id`、`owner_name`：录入人快照。
- `remark`：备注。
- `modification_log_json`：编辑日志 JSON，由后端在编辑保存时比较旧值和新值后自动追加。

## 7. 对象类型

| 类型 | 说明 |
| :--- | :--- |
| `MATERIAL` | 其他材料 |
| `FABRIC` | 面料 |
| `PACKAGING` | 包材 |
| `UMBRELLA_FRAME` | 伞架 |
| `PRODUCT` | 产品 |
| `MANUAL` | 手工对象 |

以上是系统预置类型；用户可在筛选区和新增/编辑弹窗中快捷新增自定义对象类型。自定义类型只作为本次询价台账分类值保存，不自动创建材料、产品或字典主档。

主档对象被选择后，只把对象名称、编号、规格、单位和参考价格带入当前询价记录；保存询价不会回写主档。

## 8. 验证要点

- 角色拥有 `supplier:inquiry:page` 后能在采购管理下看到 `供应商询价`。
- 打开 `/supplier/inquiry` 不应出现“系统异常”；若出现，先查 `supplier_inquiry` 表是否存在和后端日志真实异常。
- 新增询价必须选择供应商、对象类型、对象名称并填写非负单价。
- 供应商下拉必须支持本地筛选和快捷新增，新增供应商会写入供应商主档，并只带入当前询价表单的供应商、联系人和电话快照。
- 对象类型下拉支持快捷新增，新增后可立即用于筛选和表单保存。
- 询价对象下拉支持快捷新增手工对象，保存询价不会回写任何主档。
- 选择供应商后可带出联系人和电话。
- 选择主档对象后可带出对象名称、规格、单位和参考价格。
- 列表筛选能按供应商、对象类型、币种、日期和有效状态生效。
- 点击供应商名称能打开该供应商全部询价历史；供应商列表的 `历史` 按钮也能查看该供应商询价历史。
- 点击询价对象名称能打开该对象全部历史报价。
- 单价显示保留 2 位小数，起订量去掉无效尾零，整数起订量显示为整数。
- 查看详情弹窗右下角提供编辑按钮；保存编辑后再次查看详情应能看到字段级修改日志。
- 删除为逻辑删除，不物理删除数据。

## Change Logs

| 日期 | 版本号 | 变更描述 | 负责人 |
| :--- | :--- | :--- | :--- |
| 2026-05-08 | v1.5.3 | 修正供应商快捷新增后下拉列表只剩新增项的问题 | Codex |
| 2026-05-08 | v1.5.2 | 供应商询价列表调整表头顺序，并将单价和单位合并为一列 | Codex |
| 2026-05-08 | v1.5.1 | 供应商询价筛选区改为响应式网格排布，优化大屏控件宽度和按钮位置 | Codex |
| 2026-05-07 | v1.5.0 | 补充供应商列表历史抽屉入口，明确该入口只读查看供应商询价历史 | Solazhu |
| 2026-05-06 | v1.4.0 | 询价对象名称进入对象历史报价抽屉，单价展示收口到 2 位小数，起订量默认按整数展示 | Solazhu |
| 2026-05-06 | v1.3.0 | 列表操作栏去掉编辑和历史，供应商名称进入供应商全部询价历史，详情内编辑并记录字段级修改日志 | Solazhu |
| 2026-05-06 | v1.2.0 | 供应商询价弹窗的供应商字段支持快捷新增，修正供应商主档为空时无法继续录入询价的问题 | Solazhu |
| 2026-05-06 | v1.1.0 | 供应商询价入口调整到采购管理，补充缺表自初始化和对象类型/询价对象快捷新增边界 | Solazhu |
| 2026-05-06 | v1.0.0 | 新增供应商询价台账 Feature GUIDE，记录页面、接口、权限、SQL 和业务边界 | Solazhu |
