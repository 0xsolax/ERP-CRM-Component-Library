# SOURCE_MAP

## 来源项目

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/qmy-zhongsheng-ai/docs/guide/GUIDE_公共单据动作.md` | 业务规则、接口、状态和边界 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/document` | 动作日志、解锁、重新确认、负责人改派后端 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/system` | 系统操作日志后端 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-admin/src/components/document-action-log-drawer` | 前端日志抽屉 |
| `RAW/PROJECTs/qmy-zhongsheng-ai/zhongsheng-backend/docs/sql/init-document-action.sql` | 数据表 |

## 复制范围

- 已复制 document、system 操作日志后端领域。
- 已复制 document 和 system DTO。
- 已复制日志抽屉、系统操作日志页面、API 和常量。
- 已复制 SQL、GUIDE 和关键报告。

## 排除范围

- 未复制报价、订单、采购、生产具体业务单据实现。
- 未复制完整系统管理、角色和用户模块；权限基座作为依赖处理。

## 判断

该组件已经跨报价、订单、采购、生产复用，具备独立抽取价值。接入目标项目时，关键风险在于各 documentType 的可见性和业务状态适配。
