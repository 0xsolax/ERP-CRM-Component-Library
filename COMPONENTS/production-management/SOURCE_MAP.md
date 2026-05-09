# SOURCE_MAP

## 来源项目

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_生产单管理.md` | 业务规则、接口、表结构、导出和边界 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/production` | 生产组、生产总单、进度、批次和导出后端实现 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/production` | 请求 DTO |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-production.sql` | 表结构 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/views/zs/production` | 前端列表、详情、进度表 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/api/zs/production` | 前端 API 封装 |

## 复制范围

- 已复制后端生产领域、生产 DTO、生产错误码和生产单 Excel 模板。
- 已复制前端生产页面、生产 API 和生产路由。
- 已复制 `init-production.sql` 和关键 GUIDE/REPORT 证据。

## 排除范围

- 未复制整个订单、采购、产品、文件和公共单据动作代码；这些作为组件依赖处理。
- 未复制 `target/`、运行配置、依赖缓存和本地环境文件。

## 判断

该组件具备前端、后端、数据、权限、导出和 GUIDE 证据，可标记为 `reference`。但它依赖订单、采购和公共单据动作，目标项目不能把本快照当作独立 SDK 直接落地。
