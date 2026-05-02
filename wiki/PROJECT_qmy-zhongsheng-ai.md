# PROJECT_qmy-zhongsheng-ai

## 定位

`RAW/PROJECTs/qmy-zhongsheng-ai` 是中圣专用 ERP/CRM 单仓来源。它由 `zhongsheng-admin` 前端与 `zhongsheng-backend` 后端组成，目标是在中圣业务边界内承接基础资料、产品、客户、供应商、报价、订单、采购、生产、文件和权限能力。

本项目在组件库中的用途是业务复现与验收参考，不等同于可直接装配的通用组件包。组件库仍以 `COMPONENTS/*` 里的组件快照和 `wiki/COMP_*.md` 组件卡作为复用入口。

## 关键来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/README.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/AGENTS.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_项目接手引导.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_文档索引.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL53父级完成情况复核.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/pdoc/report/REPORT_0501_SOL63总体验收与回归.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql`

## 当前判断

- 前端中圣业务入口集中在 `zhongsheng-admin/src/views/zs`，API 封装集中在 `zhongsheng-admin/src/api/zs`。
- 后端业务主线集中在 `zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core`。
- 旧 demo 仓用于确认业务口径、交互逻辑、权限语义和 Excel 样板；当前单仓代码用于确认实际字段、接口、权限常量和实现方式。
- SOL-63 已形成服务级强回归和导出实物验证，但真实浏览器、真实数据库、真实角色权限链路仍需补证后才能视为完整验收。
- SOL-53 父级复核曾指出公共单据动作层对生产模块的覆盖与父计划存在缺口；继续复用该来源时不能把“服务级通过”写成“生产级闭环完成”。

## 和组件库的关系

| 方向 | 关系 |
| :--- | :--- |
| 业务真相 | 可作为中圣专用业务链路的当前实现参考 |
| 组件复用 | 不能直接替代 `COMPONENTS/*` 快照，需要按组件契约再抽取 |
| 验收证据 | 可提供 GUIDE、REPORT、脚本和导出物证据，但真实环境验收仍需单独标记 |
| 风险边界 | 财务当前仍是占位边界；公共动作、生产、权限和浏览器链路需逐项核验 |

## 后续处理

- 若后续从该仓继续抽组件，应优先从 `docs/guide/GUIDE_文档索引.md` 定位模块 GUIDE。
- 新组件卡必须区分当前单仓已实现、旧 demo 业务参考和组件库可复用契约。
- 不自动把 `pdoc/` 内执行报告写成通用组件事实；需要回到当前代码和验收证据逐项确认。

## Change Logs

| 日期 | 变更 |
| :--- | :--- |
| 2026-05-02 | SOL-40 收口后登记 qmy-zhongsheng-ai 来源项目页，标记其业务复现参考价值与验收边界 |
