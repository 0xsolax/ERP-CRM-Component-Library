# COMP 文件上传与 OSS

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | 基础设施 / 文件 |
| 复用等级 | 可参考改造 |
| 适用项目 | 产品图片、材料图片、附件、报价/订单文件 |
| 来源路径 | `RAW/PROJECTs/qmy-admin/src/components/bz-upload`、`RAW/PROJECTs/qmy-admin/src/api/admin/system/storage.ts`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/UploadController.java`、`RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file`、`RAW/PROJECTs/zhongsheng-backend/docs/sql/init-system-file.sql`、`RAW/PROJECTs/project-scaffold/docs/sql/init-system-file.sql` |

## 业务目标

为产品、材料、客户附件、报价文件等提供上传、存储记录和访问 URL 管理能力。

## 前端入口

- 上传组件：`RAW/PROJECTs/qmy-admin/src/components/bz-upload`。
- 存储记录 API：`RAW/PROJECTs/qmy-admin/src/api/admin/system/storage.ts`，路径 `/storage/saveSysStorage`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 图片上传 | POST | `/api/upload/image` | `UploadController` |
| 获取 OSS STS | GET | `/oss/getOssToken` | `OssController` |
| 保存文件记录 | POST | `/storage/saveSysStorage` | `StorageController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `system_file` | 文件名、类型、大小、endpoint、key、url | 文件存储记录 |
| `StorageDTO` | `url`、`name`、`type`、`size` | 保存文件记录入参 |
| `OssStsTokenVO` | 临时凭证、bucket、endpoint 等 | 前端直传可用 |

## 权限边界

- 上传接口通常要求登录。
- 存储配置和 OSS 密钥不得暴露给普通用户。
- 产品图片、客户附件、报价文件的访问范围应按业务模块控制。

## 接入步骤

1. 选择上传方式：后端中转上传或前端 OSS STS 直传。
2. 配置 OSS endpoint、bucket、base URL、STS role。
3. 保存文件记录到 `system_file`。
4. 业务表只保存文件 ID 或文件 URL；优先保存文件 ID。
5. 前端统一使用上传组件和存储记录 API。

## 验收清单

- [ ] 文件能上传并返回可访问 URL。
- [ ] 文件记录能保存到 `system_file`。
- [ ] 业务表能引用文件。
- [ ] OSS 密钥不出现在前端。
- [ ] 删除或替换文件有明确策略。

## 已知风险

- 旧 `UploadController` 直接使用 access key 后端中转上传；新版更适合使用 STS 和文件记录。
- 业务表保存 URL 还是文件 ID 需要项目统一，否则后续迁移 OSS 域名会有成本。

