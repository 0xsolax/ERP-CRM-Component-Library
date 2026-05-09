# PROJECT_qmy-zhongsheng-ai

## 定位

`RAW/PROJECTs/qmy-zhongsheng-ai` 是中圣专用 ERP/CRM 单仓来源。它由 `zhongsheng-admin` 前端与 `zhongsheng-backend` 后端组成，目标是在中圣业务边界内承接基础资料、产品、客户、供应商、报价、订单、采购、生产、文件、权限、观察页面和后续财务能力。

本项目在组件库中的用途是业务复现与验收参考，不等同于可直接装配的通用组件包。组件库仍以 `COMPONENTS/*` 里的组件快照和 `wiki/COMP_*.md` 组件卡作为复用入口。

## 关键来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/README.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/AGENTS.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_项目接手引导.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_文档索引.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL53父级完成情况复核.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL63总体验收与回归.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_观察页面.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/design/DESIGN_0509_财务模块.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0507_主链统一编号复刻.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0508_产品表单快捷新增补齐.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql`

## 当前判断

- 前端正式 URL 已去掉 `/zs` 前缀，登录入口为 `/login`，业务页面为 `/dashboard/index`、`/quote/add` 等无前缀路径；`/zs/*` 只作为旧测试链接和书签兼容跳转。
- 前端中圣源码短期仍集中在 `zhongsheng-admin/src/views/zs`，API 封装集中在 `zhongsheng-admin/src/api/zs`。
- 后端业务主线集中在 `zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`。
- 旧 demo 仓用于确认业务口径、交互逻辑、权限语义和 Excel 样板；当前单仓代码用于确认实际字段、接口、权限常量和实现方式。
- 5 月 4 日到 5 月 9 日新增大量补丁和验收材料，重点覆盖生产总单派生采购与快捷新增、入口收口、系统管理管理员化、供应商询价台账、观察页面、主链统一编号、导出模板和财务模块设计。
- SOL-63 已形成服务级强回归和导出实物验证；后续部分 Browser 证据已进入 `pdoc/material/`，但是否等同完整生产验收仍要按具体报告和真实环境链路判断。
- SOL-64 后续已扩展出 `DESIGN_0509_财务模块.md` 草案，财务方向从占位进入设计阶段；未完成前仍不能把真实应收/应付闭环写成已实现。

## 近期整理进展

详见 [SYNTH_qmy中圣近期整理进展](SYNTH_qmy中圣近期整理进展.md)。

当前已识别的新文档主题：

- 生产总单派生采购、快捷新增、公共单据解锁和气泡日志。
- 中圣入口从 `/zs` 兼容路径收口到正式无前缀 URL。
- 系统管理管理员化、角色权限矩阵、历史 ID 兼容。
- 订单详情抽屉、采购详情抽屉、生产详情抽屉和观察页面直达详情。
- 供应商询价台账和供应商历史查询。
- 统一业务主单号与内部流水编号。
- Excel 导出样板、生产单客户模板和旧宽表残留清理。
- 产品、报价、订单、采购、生产页面的局部交互与表单能力补齐。
- 财务模块设计草案。

## 和组件库的关系

| 方向 | 关系 |
| :--- | :--- |
| 业务真相 | 可作为中圣专用业务链路的当前实现参考 |
| 组件复用 | 已从 `solazhu-finance-0509` 当前实现补抽 `document-action`、`production-management`、`supplier-inquiry` 三个组件；其他模块仍需按组件契约逐项抽取 |
| 验收证据 | 可提供 GUIDE、REPORT、脚本和导出物证据，但真实环境验收仍需单独标记 |
| 风险边界 | 财务当前仍是占位边界；公共动作、生产、权限和浏览器链路需逐项核验 |

## 后续处理

- 若后续从该仓继续抽组件，应优先从 `docs/guide/GUIDE_文档索引.md` 定位模块 GUIDE。
- 新组件卡必须区分当前单仓已实现、旧 demo 业务参考和组件库可复用契约。
- 不自动把 `pdoc/` 内执行报告写成通用组件事实；需要回到当前代码和验收证据逐项确认。

## Change Logs

| 日期 | 变更 |
| :--- | :--- |
| 2026-05-09 | 基于 `solazhu-finance-0509` 当前实现补抽公共单据动作、生产单管理和供应商询价台账三个组件快照 |
| 2026-05-09 | 同步 5 月 4 日到 5 月 9 日新增文档主题，补充无前缀入口、观察页面、供应商询价、统一编号和财务设计边界 |
| 2026-05-02 | SOL-40 收口后登记 qmy-zhongsheng-ai 来源项目页，标记其业务复现参考价值与验收边界 |
