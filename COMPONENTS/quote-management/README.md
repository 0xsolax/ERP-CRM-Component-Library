# quote-management 报价管理组件

## 定位

`quote-management` 提供 ERP/CRM 销售链路中的报价主档、SKU 报价、采购成本确认、物流成本确认、成本明细、历史报价参考、审核会签、总裁微信审核和报价转订单能力。

本组件是可追溯快照，不是已打包 SDK。新项目接入前必须先确认报价复杂度：只需要基础 CRUD 时参考 `zhongsheng-AI`；需要成本核算、历史报价和转订单时参考 `qmy-java` 与 `qmy-admin` 的 SED 复杂流。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 业务组件 |
| 必选依赖 | `BASE/project-scaffold`、`auth-permission`、`base-data`、`product-material`、`customer-management` |
| 可选依赖 | `file-oss`、`order-management` |
| 主要来源 | `RAW/PROJECTs/zhongsheng-AI`、`RAW/PROJECTs/qmy-java`、`RAW/PROJECTs/qmy-admin`、`RAW/docs/zhongsheng` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-AI/` | PRD 对应的 legacy 报价 CRUD：`QuoteController`、`Quote`、`QuoteService`、`QuoteMapper` |
| `backend/qmy-java/` | SED 报价复杂流：报价 controller、service、manager、entity、param、VO、Mapper XML、枚举和转订单引用证据 |
| `frontend/qmy-admin/` | SED 报价列表、报价弹窗、成本明细、历史报价、导出、审核、转订单页面和 API |
| `db/` | legacy `quote/quote_item` SQL，以及 qmy-java 报价表结构说明 |
| `docs/source/` | PRD 和调研材料来源副本 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照和接入验收清单、复核证据 |

## 能力边界

已覆盖：

- 基础报价 CRUD：分页、详情、新增、编辑、删除、报价日期、有效期、总金额、状态、备注。
- 报价主档：报价编号、客户、业务员、币种、含税、装运港、指定地点、汇率、优惠金额、收货地址、特殊要求。
- SKU 报价：产品、搭配、SKU、基础报价、报价、数量、体积、备注。
- 成本确认：采购成本、物流成本、总成本和各自确认状态。
- 成本明细：配件成本、工艺/油漆成本、包材成本、SKU 图片和配件图片来源。
- 历史报价：本次报价、全部客户和本客户趋势、平均报价、中位数、毛利率参考。
- 审核流：提交审核、驳回、财务会签、总裁会签、总裁微信审核凭证。
- 转订单：一键整单转订单、单 SKU 转订单、合并多个报价 SKU 转订单。
- 前端入口：报价列表、报价新增/编辑/再次创建、采购成本、物流成本、详情、导出、历史导入、转订单。

待项目确认：

- `zhongsheng-AI` 的 `Quote` 实体存在 `products` 字段，但 legacy `quote` SQL 未定义该列；同时 `quote_item` 表在 legacy CRUD 中没有完整子表写入实现。
- `qmy-java` 未提供完整报价建表 SQL，接入时必须按实体、Mapper XML 和目标库规范补 DDL。
- `qmy-java` 来源只有列表接口标注 `@RequiresDataPermissions`，详情、成本确认、审核、历史报价和转订单需在目标项目补报价归属/客户范围守卫。
- qmy-admin 报价页依赖客户、产品、包材、文件、订单、用户 store、下载工具和全局布局，本组件只复制报价直接入口，不复制整个基座。
- 报价成本公式高度行业相关，不能把 SED 伞业口径直接当作所有项目默认口径。

## 快速接入

1. 先接入 `BASE/project-scaffold`、`auth-permission`、`base-data`、`product-material` 和 `customer-management`。
2. 判断业务复杂度：基础报价只需 `quote/quote_item`；复杂报价需 `sal_sed_quotation`、SKU、包材、历史和审核表。
3. 建立报价主表、报价 SKU、SKU 包材、报价历史操作表，并确认 `BaseEntity` 的租户和软删除字段。
4. 接入后端报价列表、详情、新增编辑、成本确认、审核、历史报价、导出和转订单接口。
5. 接入前端 `quotation` 页面、API、状态常量和销售管理路由入口。
6. 配置权限码：列表、详情、新增编辑、成本确认、提交审核、会签审核、历史报价、导出、转订单。
7. 对报价详情、历史报价和转订单接口补充报价归属、客户范围、租户范围和敏感字段控制。
8. 按 `docs/acceptance/ACCEPTANCE.md` 做快照验收和后续装配验收。

## 安全与业务规则

- 报价包含客户、联系人地址、价格、成本、毛利率、汇率和审核凭证，必须按后端权限和数据范围控制。
- 历史报价参考只应暴露业务允许的客户维度；跨客户报价趋势需要明确脱敏或角色限制。
- 转订单必须校验报价状态、客户一致性、业务员一致性、SKU 是否重复、是否已转换，避免重复生成订单。
- 成本明细里的产品、配件、包材和工艺成本应保存报价时点快照，不能只依赖产品实时成本。
- 币种和汇率必须进入报价快照；历史均价/中位数如统一折算人民币，需要在接口和 UI 中明确口径。
- 微信审核图片依赖 `file-oss`，不得在报价组件中硬编码文件访问密钥或公开 URL 策略。
