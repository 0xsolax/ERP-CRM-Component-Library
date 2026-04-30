# PROJECT zhongsheng-AI

## 定位

`zhongsheng-AI` 是雨伞制造企业 ERP 的旧实现和需求资料来源。它对 ERP/CRM 组件库的主要价值是客户、报价、订单、产品、材料、供应商、财务和上传等业务闭环证据。

## 来源路径

- `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md`
- `RAW/PROJECTs/zhongsheng-AI/README.md`
- `RAW/PROJECTs/zhongsheng-AI/erp-frontend`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/service`
- `RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql`

## 已确认业务模块

- 基础信息：产品、产品分类、工价、字典。
- 材料：伞架、面料、其他材料、包材。
- 销售：客户、报价、订单。
- 采购：供应商。
- 财务：财务记录。
- 系统：用户、角色、权限。
- 文件：图片上传到 OSS。

## 可贡献组件

| 组件 | 证据 | 复用价值 |
| :--- | :--- | :--- |
| 客户管理 | `CustomerController`、`customer` 表、PRD 客户管理章节 | 客户档案、标签、客户类型/层级需求 |
| 报价管理 | `QuoteController`、`quote`/`quote_item` 表、PRD 报价章节 | 报价单、成本核算、历史报价参考 |
| 订单管理 | `OrdersController`、`orders`/`order_item` 表、PRD 订单章节 | 报价转订单、订单状态、交期 |
| 产品物料基础数据 | `ProductController`、`MaterialController`、`UmbrellaFrameController`、`init.sql` | 伞类产品 BOM、成本、基础资料 |
| 采购供应商 | `SupplierController`、`supplier` 表、调研需求 | 供应商档案和采购信息基础 |
| 文件上传与 OSS | `UploadController` | 图片上传路径和 OSS 直传前的后端上传实现 |

## 待验证

- 旧后端接口多为 REST CRUD，权限和菜单粒度不如新版 `zhongsheng-backend` 完整。
- PRD 是按钮级需求，不等于全部已实现；组件卡引用 PRD 时需区分“需求事实”和“代码事实”。

