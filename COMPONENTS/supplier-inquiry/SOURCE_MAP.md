# SOURCE_MAP

## 来源项目

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_供应商询价台账.md` | 功能定位、接口、权限、数据表和验收要点 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/supplier` | 供应商与询价后端实现 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-supplier-inquiry.sql` | 询价表结构 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/supplier-inquiry` | 前端台账页面 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs/supplier-inquiry` | 前端 API 封装 |

## 复制范围

- 已复制供应商领域代码，包含询价和供应商主档依赖。
- 已复制询价 DTO、缺表兜底初始化器、SQL、前端页面、API 和采购路由。
- 已复制 GUIDE 和相关报告。

## 排除范围

- 未复制产品、材料、面料、包材、伞架的完整主档实现；它们作为 `product-material` 依赖处理。
- 未复制采购单生成、入库和付款能力。

## 判断

该组件已具备前端、后端、数据、权限和编辑日志证据，可标记为 `reference`。但它是采购前台账，不应被误用为正式比价审批或采购生成组件。
