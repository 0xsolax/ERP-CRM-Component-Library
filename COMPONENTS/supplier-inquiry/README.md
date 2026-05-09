# supplier-inquiry 供应商询价台账

## 定位

本组件沉淀采购前询价历史能力，用于记录供应商对材料、面料、包材、伞架、产品或手工对象的报价条件，辅助后续人工比价和采购决策。

它不自动生成采购单，不回写主档价格，也不进入库存、入库或付款链路。

## 能力清单

- 供应商询价新增、编辑、详情、删除和分页筛选。
- 按供应商或询价对象查看历史价格。
- 供应商下拉内快捷新增供应商并回填。
- 询价对象支持主档选择和手工对象。
- 单价/单位、起订量、交期、税率、联系人和有效期记录。
- 编辑保存自动记录字段级修改日志。
- 采购管理下独立菜单入口。

## 来源

- `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-supplier-inquiry.sql`
- `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/supplier-inquiry/index.vue`

## 接入步骤

1. 先接入供应商主档、产品物料和权限基座。
2. 创建 `supplier_inquiry` 表。
3. 接入询价台账页面、API 和采购管理菜单。
4. 接入供应商快捷新增和主档对象查询。
5. 按验收清单覆盖历史抽屉、编辑日志、权限和逻辑删除。

## 边界

- 不作为正式比价审批单。
- 不自动采用报价生成采购单。
- 不回写材料、产品、供应商主档价格。
- 不处理付款、入库、库存和供应商评分。
