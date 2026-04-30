# COMP 采购供应商

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | ERP / 采购 |
| 复用等级 | 待验证 |
| 适用项目 | 制造、外采、供应商管理 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/views/admin/purchase`、`RAW/PROJECTs/qmy-admin/src/api/admin/purchase`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/SupplierController.java`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`、`RAW/docs/zhongsheng` |

## 业务目标

维护供应商档案，并在订单或采购申请阶段按供应商生成采购单，支持采购跟进、退货、库存预警和供应商选择。

## 前端入口

- 供应商：`RAW/PROJECTs/qmy-admin/src/views/admin/purchase/supplier`。
- 已采购：`RAW/PROJECTs/qmy-admin/src/views/admin/purchase/purchased`。
- 待采购：`RAW/PROJECTs/qmy-admin/src/views/admin/purchase/pending`。
- 库存预警：`RAW/PROJECTs/qmy-admin/src/views/admin/purchase/stock-warning`。
- API：`RAW/PROJECTs/qmy-admin/src/api/admin/purchase`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 供应商分页 | GET | `/api/supplier/page` | `SupplierController` |
| 供应商详情 | GET | `/api/supplier/{id}` | `SupplierController` |
| 新增供应商 | POST | `/api/supplier` | `SupplierController` |
| 编辑供应商 | PUT | `/api/supplier` | `SupplierController` |
| 删除供应商 | DELETE | `/api/supplier/{id}` | `SupplierController` |
| 采购列表 | POST | `/pur/yt/purchase/list` | `qmy-admin` API |
| 采购创建/更新 | POST | `/pur/yt/purchase/createOrUpdate` | `qmy-admin` API |
| 待采购申请 | POST | `/pur/yt/applyPurchase/*` | `qmy-admin` API |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `supplier` | `code`、`name`、`contact`、`phone`、`email`、`address`、`status` | 供应商主档 |
| 采购单 | 供应商、订单号、配件/产品、采购价格、交期、数量 | 调研资料明确，当前本轮未找到完整 SQL |

## 权限边界

- 调研资料强调报价阶段不应强绑定供应商，采购阶段再选择供应商。
- 采购、财务、老板视角的数据范围需分别确认。

## 接入步骤

1. 先确认采购是否从订单、库存预警或手工申请触发。
2. 供应商主档先接入基础 CRUD。
3. 采购单按单个供应商聚合明细。
4. 明确同一配件跨订单采购时的分摊与追踪方式。
5. 与订单、仓库、财务应付模块对齐。

## 验收清单

- [ ] 供应商可新增、编辑、停用或删除。
- [ ] 采购单能按供应商维度生成。
- [ ] 采购明细能关联订单或物料。
- [ ] 采购状态和跟进记录可追踪。
- [ ] 报价阶段与采购阶段供应商边界不混淆。

## 已知风险

- 本轮只确认到供应商后端 CRUD 和 qmy-admin 采购前端 API；采购单后端完整模型仍待深挖。
- 采购规则强依赖企业岗位流程，不宜直接泛化。

