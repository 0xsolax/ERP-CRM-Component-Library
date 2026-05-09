# SYNTH_qmy中圣近期整理进展

## 定位

本页汇总 `RAW/PROJECTs/qmy-zhongsheng-ai` 在 2026-05-04 到 2026-05-09 期间新增和重写的大量文档。它是项目级进展索引，不替代具体 GUIDE、DESIGN、REPORT 或源码证据。

## 来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/log/memory.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_项目接手引导.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_文档索引.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_观察页面.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/design/DESIGN_0509_财务模块.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0507_主链统一编号复刻.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_产品表单快捷新增补齐.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_生产单旧宽表残留清理.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_生产总单详情抽屉产品行交货合并.md`

## 已确认主题

### 入口与导航

- 正式 URL 已从 `/zs/*` 收口到无前缀路径，例如 `/login`、`/dashboard/index`、`/quote/add`。
- `views/zs` 仍是短期业务源码目录，`admin` MPA 入口壳用于承载中圣应用。
- 侧边栏一级导航收束为仪表盘、产品管理、销售管理、生产管理、财务管理、系统管理。
- 观察页面成为登录后的第一工作页；工具箱入口保留隐藏，不在侧边栏开放。

### 主业务链路

- `SOL-69 / MIG-11` 将生产总单详情提升为对内执行合同和派生采购入口。
- 报价、订单、生产总单、采购单已经围绕统一业务主单号和内部流水进行复刻：前台可见编号沿链路保持一致，内部流水按阶段递增。
- 订单详情、采购详情、生产总单详情逐步抽屉化或直达化，减少从观察页面和列表页进入详情后的二次搜索。
- 公共单据解锁从“申请/审批”收口到管理员解锁和重新确认，报价、订单、采购、生产均接入公共单据日志。

### 主数据与快捷新增

- 产品表单继续补齐快捷新增入口：伞架、工序、印刷方式、对齐方式等可以在表单内补齐。
- 报价结构编辑器从自由文本升级为结构化快照维护，支持仅应用当前报价或同步产品主档。
- 供应商下拉、对象类型和询价对象在供应商询价台账中支持快捷新增，但询价记录不会回写主档价格或生成采购单。

### 采购、生产与询价

- 供应商询价台账成为采购前价格事实层，支持供应商维度、询价对象维度和字段级编辑日志。
- 采购详情抽屉、订单排产采购进度、供应商历史、采购入库调整等逐步补齐。
- 生产单导出已收口到客户单品 `.xls` 模板，旧 A:L 宽表布局残留已清理。
- 生产总单详情抽屉中产品行进度和交货入口合并，避免重复维护“产品行交货”表。

### 导出与验收

- 报价单、PI、采购单、生产单、历史报价持续对齐旧仓或客户提供的 Excel 样板。
- 导出修复包括模板填值、图片 URL 空格转义、可信 OSS 图片限制、JSON 错误体识别、生产单旧宽表清理。
- 5 月 6 日以后出现大量 Browser 回归截图和 JSON 证据，适合作为验收材料索引；不应把截图内容自动转换成业务事实。

### 财务方向

- `DESIGN_0509_财务模块.md` 将财务从占位推进到设计草案。
- 设计判断是一期开“业务财务台账”，先做订单应收和采购应付账本，再做客户/供应商往来聚合，最后补财务概览和统一收付款记录。
- 明确非目标：不做总账、发票、审批、自由流水、预收预付、跨币种汇总，也不直接复用旧仓通用 `finance`。
- 财务设计仍是草案，待业务确认前不能写成已实现组件。

## 当前边界

- `qmy-zhongsheng-ai` 是中圣专用实现来源，不直接替代 `COMPONENTS/*` 组件快照。
- 2026-05-09 已从当前 `solazhu-finance-0509` 实现补抽 3 个组件：`document-action`、`production-management`、`supplier-inquiry`。
- 财务仍停留在设计草案；`form-config` 更接近基座/前端表格配置能力，暂不写成业务组件。
- 大量 `pdoc/material/` 浏览器截图、JSON、Excel 是验收证据，默认只登记为来源，不深度解读其中的客户或环境细节。
- `RAW/` 来源层不改写；本轮只更新 `wiki/`、manifest 和本地 `pdoc/` 报告。

## 后续建议

1. 继续复核 `purchase-supplier` 是否可用当前 qmy 采购履约实现从 `draft` 升级到 `reference`。
2. 将 `form-config`、`bz-table` 和 `@FormSchema` 另行评估为基座能力，而不是混进业务组件。
3. 新增 Browser 证据较多，后续可以单独建立验收证据索引页，避免 `SOURCE_INDEX.md` 被截图清单淹没。

## Change Logs

| 日期 | 变更 |
| :--- | :--- |
| 2026-05-09 | 基于当前分支补抽公共单据动作、生产单管理和供应商询价台账三个组件快照 |
| 2026-05-09 | 首次汇总 qmy-zhongsheng-ai 近期新增文档主题和组件库复用边界 |
