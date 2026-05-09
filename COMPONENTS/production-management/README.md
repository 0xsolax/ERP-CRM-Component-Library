# production-management 生产单管理

## 定位

本组件沉淀中圣新基座中的生产履约能力：订单确认后创建或复用唯一生产总单，围绕产品行进度推进采购入库释放、分批安排、产品行交货和生产单导出。

它不是完整 MES，也不包含库存扣减、工序报工、质量检验或客户签收台账。

## 能力清单

- 生产组主档维护。
- 来源订单唯一生产总单。
- 手工新增不关联订单的生产总单。
- 产品行进度：订单数量、已采购、已入库、已安排、已交货、剩余待交。
- 生产总单列表和详情中的安排采购入口。
- 分批安排生产并刷新产品行已安排数量。
- 产品行本次交货并累加已交货数量。
- 生产总单公共单据日志、管理员解锁、重新确认和负责人改派接入。
- 读取客户样板 `production-order-template.xls` 导出生产单。

## 来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_生产单管理.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/production`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/production`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-production.sql`

## 接入步骤

1. 先接入基座、认证权限、产品物料、订单管理和公共单据动作。
2. 创建 `production_group`、`production_order`、`production_order_progress`、`production_order_batch`。
3. 接入生产组、生产总单列表、详情、进度表和导出接口。
4. 将订单确认动作接入生产总单创建或刷新逻辑。
5. 若接入采购组件，保证采购生成和入库刷新既有生产总单，而不是隐式创建空生产总单。
6. 按验收清单覆盖生产安排、交货、导出、权限和日志。

## 边界

- 不做完整排产系统。
- 不做生产工序完工回报。
- 不做库存扣减和仓位流转。
- 不做独立客户签收交货台账。
- 手工生产单当前不派生采购单。
