# GUIDE_生产单管理

| 字段 | 内容 |
| :--- | :--- |
| 文档类型 | GUIDE |
| 版本号 | v1.6.6 |
| 创建日期 | 2026-05-01 |
| 负责人 | Solazhu |
| 适用范围 | SOL-62 / MIG-08 生产组、生产总单与交货进度 |

---

## 1. 功能定位

生产单管理承接确认订单后的生产履约视角。当前新基座不再用多张生产表头表达同一订单进度，而是为每张已确认订单创建或复用一张唯一生产总单；产品行进度、分批安排、采购入库释放和累计交货都在这张总单下推进。 - v1.0.0

本轮已对照旧 demo 仓：

```text
/Users/solazhu/software/zhongsheng-ai/docs/guide/GUIDE_生产单管理.md
```

采纳的旧口径：

- 订单确认后创建或复用唯一生产总单。 - v1.0.0
- 生产组是正式主档，字段收口为编码、名称、状态、备注。 - v1.0.0
- 生产安排写入总单下的分批安排，不再为同一订单创建多张生产表头。 - v1.0.0
- 产品行进度展示订单数量、已采购、已入库、已安排、已交货、剩余待交和状态。 - v1.0.0
- 采购入库反哺生产总单产品行，释放可安排生产数量；不会因为采购入库自动代建生产总单。 - v1.0.0
- 生产总单详情可作为对内执行合同入口，从同一张来源订单快照派生多张采购单，并显式携带当前生产总单 ID。 - v1.1.0
- 生产总单保留公共单据解锁机制，当前前端只保留管理员“解锁”、重新确认和气泡日志口径；申请解锁/审批入口暂隐藏。 - v1.3.1
- 生产总单列表支持单独新增不绑定来源订单的手工生产单，产品行来自产品主档快照，后续可安排生产、交货和导出。 - v1.3.0
- 生产总单列表行和详情工具栏均提供“安排采购”入口，复用订单页左右分栏安排采购抽屉，不再维护简化版生成采购抽屉。 - v1.4.1
- 生产总单产品行进度和产品行交货表展示产品图片，图片从产品快照读取；`released` 状态按可安排数量展示为“可安排生产”或“待采购入库”。 - v1.4.2
- 生产总单详情抽屉中，产品行进度和产品行交货合并到同一张产品行表，交货按钮位于进度表操作列；抽屉宽度按视口放宽，避免产品列表和交货表上下重复占用空间。 - v1.6.6
- 产品行交货按“本次交货数量”录入，后端累加到累计已交货，避免前后端累计/增量口径漂移。 - v1.0.0
- 生产导出将产品图片放入客户样板 `A21:D31`，材料区按产品快照列出伞架、伞头、伞尾、伞珠、伞布等明确描述。`.xls` 模板必须使用 HSSF 图片锚点，避免通用 EMU 偏移导致图片覆盖整张生产单。旧 A:L 多产品宽表版式和相关程序化布局代码均不得再保留。 - v1.6.5

本轮取舍：

- 页面按当前新基座 Element Plus 列表、抽屉和权限按钮重建，不照搬旧 demo 组件。 - v1.0.0
- 生产导出恢复客户样张核心列序和字段，材料列直接读取订单/生产产品快照，不从采购单或供应商反推生产材料描述。 - v1.6.3
- 当前不做完整排产系统、多订单合并生产、工序完工回报、库存扣减和客户签收交货台账。 - v1.0.0
- 生产组下拉允许在生产安排时就地快捷新增，前端显隐受生产组保存权限控制，后端保存接口仍负责最终权限和校验。 - v1.1.0

## 2. 前端入口

| 页面 | 文件 |
| :--- | :--- |
| 生产组列表/新增/编辑 | `zhongsheng-admin/src/views/zs/production/group.vue` |
| 生产总单列表/新增/详情/安排/交货/派生采购/导出 | `zhongsheng-admin/src/views/zs/production/order.vue` |
| 生产总单进度复用组件 | `zhongsheng-admin/src/views/zs/production/components/production-progress-table.vue` |
| 生产 API | `zhongsheng-admin/src/api/zs/production/index.ts` |
| 路由 | `zhongsheng-admin/src/views/zs/router/async-modules/production.ts` |

当前路由：

| 路由 | 说明 | 权限 |
| :--- | :--- | :--- |
| `/production/group` | 生产组主档 | `production:group:page` |
| `/production/order` | 生产总单列表，可通过 `orderId` 查询来源订单生产总单 | `production:order:page` |

进度展示入口：

- 订单详情页展示 `productionMaster` 的总单进度。 - v1.0.0
- 采购详情中的来源订单预览展示同一张总单进度。 - v1.0.0
- 生产总单详情展示产品行进度、产品行交货入口和分批安排；产品行交货不再单独维护第二张重复产品表。 - v1.6.6
- 生产总单详情提供来源订单、按当前生产总单过滤的采购单列表和安排采购入口；列表行与详情工具栏均复用订单页左右分栏安排采购抽屉。 - v1.4.1

## 3. 后端入口

| 接口 | 说明 | 权限 |
| :--- | :--- | :--- |
| `POST /production/group/page` | 生产组分页 | `PRODUCTION_GROUP_PAGE` |
| `POST /production/group/options` | 生产组下拉 | `PRODUCTION_GROUP_PAGE` |
| `POST /production/group/detail` | 生产组详情 | `PRODUCTION_GROUP_DETAIL` |
| `POST /production/group/saveOrUpdate` | 保存生产组 | `PRODUCTION_GROUP_SAVE_OR_UPDATE` |
| `POST /production/group/delete` | 删除生产组 | `PRODUCTION_GROUP_DELETE` |
| `POST /production/order/page` | 生产总单分页 | `PRODUCTION_ORDER_PAGE` |
| `POST /production/order/saveOrUpdate` | 保存手工生产总单 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/product/page` | 手工生产单选品分页 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/productSnapshot` | 手工生产单产品快照 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/detail` | 生产总单详情 | `PRODUCTION_ORDER_DETAIL` |
| `POST /production/order/detailByOrder` | 按来源订单查询生产总单 | `PRODUCTION_ORDER_DETAIL` |
| `POST /production/order/batch` | 在总单下追加分批生产安排 | `PRODUCTION_ORDER_PROGRESS` |
| `POST /production/order/progress/delivery` | 产品行本次交货 | `PRODUCTION_ORDER_DELIVERY` |
| `POST /production/order/export` | 导出生产总单 Excel | `PRODUCTION_ORDER_EXPORT` |
| `POST /document/actionLog/page` | 生产总单动作日志 | `DOCUMENT_ACTION_LOG_LIST` |
| `POST /document/unlock/*` / `POST /document/reconfirm` | 生产总单解锁和重新确认；申请/审批接口保留兼容但前端入口暂隐藏 | `DOCUMENT_UNLOCK_REQUEST` / `DOCUMENT_UNLOCK_APPROVE` |

后端主要文件：

- `zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/production/**`
- `zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/production/**`
- `zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/error/ProductionErrorCodeConstants.java`

## 4. 数据表

初始化脚本：

- `zhongsheng-backend/docs/sql/init-production.sql`

新增表：

| 表 | 说明 |
| :--- | :--- |
| `production_group` | 生产组主档 |
| `production_order` | 订单唯一生产总单 |
| `production_order_progress` | 生产总单产品行进度 |
| `production_order_batch` | 生产总单分批安排 |

关键字段：

| 字段 | 说明 |
| :--- | :--- |
| `production_order.order_type` | 来源订单生产总单为 `master`，手工新增生产单为 `standalone` |
| `master_order_key` | `master` 为订单 ID 字符串；`standalone` 为手工生产单自身稳定键 |
| `active_master_order_key` | 非删除 `master` 总单唯一辅助列 |
| `lock_state` | 接入公共单据动作层，支持 `open / locked / pending_unlock / temporary_unlocked` |
| `needs_reconfirm` / `reconfirm_scope_json` | 解锁修改后的重新确认标记和范围 |
| `production_order_progress.line_key` | 订单产品行键 |
| `order_qty / planned_qty / purchased_qty / inbound_qty / delivered_qty` | 订单、安排、采购覆盖、入库释放和交货数量 |
| `progress_status` | `pending / released / scheduled / delivering / completed / manual_reconcile` |
| `production_order_batch.production_group_id / production_group_name` | 生产组 ID 与名称快照 |

## 5. 业务流程

### 5.1 订单确认创建生产总单

1. 用户确认订单。 - v1.0.0
2. 后端将订单产品快照标准化后，把订单状态置为 `confirmed`。 - v1.0.0
3. 生产服务按 `orderId` 查询总单；存在则复用，不存在则创建 `SC` 前缀生产总单。 - v1.0.0
4. 后端按订单产品行生成或刷新 `production_order_progress`。 - v1.0.0
5. 订单详情返回 `productionMaster`，前端直接展示同一总单进度。 - v1.0.0

### 5.2 手工新增生产总单

1. 用户在生产总单列表点击新增生产单。 - v1.3.0
2. 前端填写可选生产单号、客户、交期、备注，并从产品主档选择产品生成当前生产单产品快照。 - v1.3.0
3. 后端校验手工生产单号不与任一现有主链编号冲突；留空时自动生成业务主单号，生产单内部流水使用 `_2` 阶段号。 - v1.5.0
4. 手工生产单写入 `order_type=standalone`，不写来源订单 ID，产品行直接写入 `production_order_progress`。 - v1.3.0
5. 手工生产单可继续走安排生产、产品行交货和导出；当前不从手工生产单派生采购单，避免无来源订单快照时采购候选口径不清。 - v1.3.0

### 5.3 采购入库释放待投产

- 从订单生成采购单、删除草稿采购单、取消未入库采购单、数量入库、整单入库都会触发生产进度刷新。 - v1.0.0
- 从订单生成采购单时会写入既有生产总单 ID 到采购表头和明细，后续入库流水也沿用该 ID；若总单不存在则拒绝生成，不再产生空生产总单追溯。 - v1.1.1
- 从生产总单列表或详情安排采购时，前端传入 `productionOrderId`；后端校验该生产总单必须属于来源订单，避免采购单挂错内部执行合同。 - v1.4.1
- 已取消采购单不参与生产进度计算。 - v1.0.0
- `purchasedQty` 与 `inboundQty` 按订单产品行聚合；同产品行多个采购模组按 `moduleKey` 汇总后取最小覆盖量，避免多模组重复累加。 - v1.0.0
- 如果某产品行已经有采购依赖，则可安排量以入库释放数量为上限；没有采购依赖的产品行可按订单数量直接安排。 - v1.0.0
- 采购入库只刷新既有生产总单，不自动创建生产总单。 - v1.0.0

### 5.4 分批安排

1. 用户在生产总单详情点击安排生产。 - v1.0.0
2. 前端选择产品行、生产组、批次数量、计划交期和备注；生产组缺失时可在下拉 footer 直接新增并回填。 - v1.1.0
3. 后端校验生产组启用、进度行属于当前总单、批次数量大于 0 且不超过可安排数量。 - v1.0.0
4. 保存后新增 `production_order_batch`，并回刷产品行 `plannedQty`。 - v1.0.0

### 5.5 生产总单派生采购

1. 用户在生产总单列表行或详情工具栏点击安排采购。 - v1.4.1
2. 前端读取来源订单产品快照，生成成品、材料、面料、包材、印刷、伞架等候选采购项。 - v1.1.0
3. 用户在左侧“待采购项目”选择供应商和数量后安排到右侧“安排采购内容”；供应商缺失时可就地快捷新增并回填当前行。 - v1.4.1
4. 来源订单确认后生成的生产总单沿用订单 `base_code/code`，生产单内部流水为 `base_code_2`；若后续一单拆多张生产单，继续递增为 `_2_2`、`_2_3`。 - v1.5.0
5. 后端仍调用 `/purchase/generateFromOrder`，但请求体额外传入当前 `productionOrderId`；从生产总单跳转采购列表时也携带该 ID 作为列表过滤和二次生成的防错依据。 - v1.1.1
6. 后端校验生产总单与来源订单一致后，按供应商拆出多张采购单，并把生产总单 ID 写入采购表头、明细和后续入库流水。 - v1.1.0

### 5.6 产品行交货

- 用户在生产总单详情按产品行录入本次交货数量。 - v1.0.0
- 后端校验本次数量不能超过剩余待交数量，然后累加到 `deliveredQty`。 - v1.0.0
- 当前不建独立交货台账；如果后续要按日期、客户签收或批次追踪，应另开交货台账任务。 - v1.0.0

### 5.7 导出

生产导出使用 Apache POI 读取仓内模板副本并填值生成 `.xls`。 - v1.6.0
当前模板来源为客户样板 `ZSL201 生产单.xls`，已清理样板数据与内嵌图片后落到新基座 `zhongsheng-core/src/main/resources/excel/production-order-template.xls`；导出过程只读模板副本，不回写模板源文件。 - v1.6.0
旧版 `production-order-template.xlsx` 已移除，运行时不得再引用旧模板。 - v1.6.1
旧 A:L 多产品宽表样式属于错误模板设计，仓内不得保留对应模板文件或后端手写宽表布局 helper；如果浏览器下载结果仍出现 `产品图片/架子/面料/手柄/伞帽` 这类横向宽表，应优先确认是否打开了旧下载文件、命中了远端测试服或运行了未重启的旧后端进程。 - v1.6.5
客户样板是单品生产单版式；当一个生产总单包含多个产品行时，导出按产品行生成多张工作表，每张工作表保留同一张客户样板布局。 - v1.6.0

核心样张字段：

- 标题：`中圣伞业有限公司生产单`。 - v1.6.0
- 表头：单号、品名、下单日期、数量、规格、交货日期。 - v1.6.0
- 材料区：伞架、伞头、伞尾、伞珠、伞布、伞攀、帽花、顶标、布标、织标、吊牌、布套、关封、玻璃袋、纸箱。 - v1.6.0
- 下方区块：颜色分配及印刷、包装明细及唛头、样品及大货加工要求、侧唛、业务员/业务经理/日期。 - v1.6.0

材料处理：

- 导出按产品行快照填充伞架、伞头、伞尾、伞珠、伞布、伞攀、帽花、顶标、布标、织标、吊牌、布套、关封、玻璃袋和纸箱；不再从采购单、入库行或供应商名称生成材料明细。 - v1.6.3
- 材料明细展示产品主档/报价快照中的材料名称、规格、用量和装箱数等明确描述，避免出现 `001 x1 / 供应商` 这类采购行语义。 - v1.6.3
- 生产单导出将产品图片插入客户模板 `A21:D31` 区域；`.xls` 导出使用 HSSF 坐标锚点约束在该合并单元格内。 - v1.6.4
- 前端导出会识别 JSON 错误体并提示，不把错误响应落成假 Excel。 - v1.0.1

## 6. 权限与数据可见性

- 接口权限仍以 `ApiPermissionConstants` 为唯一维护处。 - v1.0.0
- 页面按钮使用 `ZS_PERMISSIONS.production.*` 控制显隐，后端接口权限是最终拦截。 - v1.0.0
- 手工生产总单新增、选品和快照接口均使用 `production:order:save`。 - v1.3.0
- 生产总单动作日志和解锁按钮复用 `ZS_PERMISSIONS.document.*`，后端 `DocumentActionService` 以 `documentType=production` 做单据可见性和管理员动作校验。 - v1.2.0
- 超级管理员拥有 `*` 时可见全部生产总单。 - v1.0.0
- 普通用户当前按生产总单 `owner_id` 可见；总单负责人继承来源订单负责人。 - v1.0.0

## 7. 当前边界

1. 不迁移旧生产数据。 - v1.0.0
2. 不做完整排产系统、工序完工、库存扣减、质量检验和多订单合并生产。 - v1.0.0
3. 不做按客户签收日期的独立交货台账，当前只保存产品行累计交货数量。 - v1.0.0
4. 当前生产总单已接入公共单据动作层的日志、解锁、重新确认和改派；申请解锁/审批入口暂隐藏，生产安排、交货等业务动作的详细日志仍需随后续生产动作细化继续补齐。 - v1.3.1
5. 手工生产单当前不派生采购单；若后续客户要求手工生产单也拆采购，需要另行设计无来源订单产品快照到采购候选项的映射规则。 - v1.3.0
6. 当前已完成静态构建和后端编译；真实数据库浏览器联调留到总体验收。 - v1.0.0

## Change Logs

| 日期 | 版本号 | 变更描述 | 负责人 |
| :--- | :--- | :--- | :--- |
| 2026-05-08 | v1.6.6 | 生产总单详情抽屉合并产品行进度与交货入口，并放宽抽屉宽度 | Codex |
| 2026-05-08 | v1.6.5 | 清理生产单旧 A:L 多产品宽表程序化布局残留，并记录旧宽表排查口径 | Codex |
| 2026-05-08 | v1.6.4 | 生产单导出图片锚点切换为 HSSF 坐标，防止图片覆盖整张表 | Codex |
| 2026-05-07 | v1.6.3 | 生产单导出材料明细改为读取产品快照，并将产品图片插入模板 A21:D31 | Codex |
| 2026-05-07 | v1.6.2 | 生产单导出移除产品图片插入，并去除材料明细中的供应商名称 | Codex |
| 2026-05-07 | v1.6.0 | 生产单导出模板替换为客户 `ZSL201 生产单.xls` 单品版式，导出改为 `.xls`，多产品按产品行拆分工作表 | Codex |
| 2026-05-07 | v1.5.0 | 复刻旧仓主链编号口径：生产总单沿用来源订单业务主单号，生产内部流水为 `_2`，支持同主单号下多张生产单扩展 | Codex |
| 2026-05-07 | v1.4.2 | 生产总单产品行进度与交货表补产品图片列，并将 released 文案按可安排数量拆为“可安排生产/待采购入库” | Codex |
| 2026-05-07 | v1.4.1 | 生产总单列表行与详情工具栏统一为“安排采购”，复用订单页左右分栏安排采购抽屉 | Codex |
| 2026-05-05 | v1.4.0 | Excel 导出样板移植：迁入旧仓生产单模板资源，生产导出改为读取模板副本并保留样张样式 | Solazhu |
| 2026-05-05 | v1.3.1 | 收口生产总单公共解锁口径：前端只保留“解锁”和重新确认，申请/审批接口仅作兼容 | Solazhu |
| 2026-05-04 | v1.3.0 | 浏览器批注修正：生产总单列表支持手工新增不绑定来源订单的生产单，并复用产品快照、安排生产、交货和导出链路 | Solazhu |
| 2026-05-04 | v1.2.0 | 保留生产总单公共解锁机制，接入 `documentType=production` 日志、解锁、审批、重新确认和负责人改派入口 | Solazhu |
| 2026-05-04 | v1.1.1 | SOL-69 Review 后补齐生产总单采购列表 productionOrderId 过滤，并禁止生成空生产总单追溯采购 | Solazhu |
| 2026-05-04 | v1.1.0 | SOL-69 补齐生产总单详情派生采购入口、生产总单 ID 校验和生产组/供应商快捷新增口径 | Solazhu |
| 2026-05-01 | v1.0.1 | 按严格 Review 补齐生产导出材料/图片口径、采购单与入库流水生产总单 ID 追溯和导出错误体识别 | Solazhu |
| 2026-05-01 | v1.0.0 | SOL-62 新增生产单管理 Feature GUIDE，记录旧仓生产 Guide 对照、生产组、生产总单、采购入库反哺、分批安排、交货与导出口径 | Solazhu |
