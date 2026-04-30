# SOURCE_MAP｜file-oss

## 来源摘要

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/zhongsheng-backend` | OSS STS、文件记录、system_file 表、租户配置项 |
| `RAW/PROJECTs/qmy-admin` | `bz-upload` 前端上传组件与存储记录 API |
| `RAW/PROJECTs/zhongsheng-AI` | 旧后端中转上传控制器，用于迁移对比 |

## 已复制范围

### zhongsheng-backend

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-core/.../core/file` | `backend/zhongsheng-backend/zhongsheng-core/.../core/file` | OSS token、文件记录 controller/service/manager/dao/entity/VO |
| `zhongsheng-api/.../dto/file` | `backend/zhongsheng-backend/zhongsheng-api/.../dto/file` | `StorageDTO`、`SystemFileDTO` |
| `zhongsheng-common/.../TenantConfigCodeConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../TenantConfigCodeConstants.java` | OSS/STs 配置项编码 |
| `zhongsheng-common/.../TenantConfigCodeEnum.java` | `backend/zhongsheng-backend/zhongsheng-common/.../TenantConfigCodeEnum.java` | OSS/STs 配置项名称与默认值 |
| `zhongsheng-common/.../OssErrorCodeConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../OssErrorCodeConstants.java` | OSS 配置缺失与 STS 获取失败错误码 |
| `zhongsheng-common/.../StrUtils.java` | `backend/zhongsheng-backend/zhongsheng-common/.../StrUtils.java` | 从 URL 解析 endpoint 与 file_key |
| `docs/sql/init-system-file.sql` | `db/zhongsheng-backend/init-system-file.sql` | `system_file` DDL |
| `docs/sql/init-tenant.sql` | `db/zhongsheng-backend/init-tenant.sql` | `tenant_config` DDL |

### qmy-admin

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `src/components/bz-upload` | `frontend/qmy-admin/src/components/bz-upload` | 上传组件，获取 STS 并直传 OSS |
| `src/api/admin/system/storage.ts` | `frontend/qmy-admin/src/api/admin/system/storage.ts` | 保存文件记录 API |

### zhongsheng-AI

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `erp-backend/src/main/java/com/erp/controller/UploadController.java` | `backend/zhongsheng-ai-legacy/src/main/java/com/erp/controller/UploadController.java` | 旧后端中转上传方案 |

## 已排除或清理

| 内容 | 处理 | 原因 |
| :--- | :--- | :--- |
| `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/` | 未复制 | 污染文件或构建缓存 |
| `application-local.yml`、`application-dev.yml`、`application-prod.yml`、`.env` | 未复制 | 可能包含真实环境配置 |
| 完整 `zhongsheng-backend/README.md` | 未复制 | 含有与 file-oss 无关的认证、调试 token、JWT 示例，避免污染组件快照 |
| 真实 OSS AccessKey、Token、数据库连接 | 未复制 | 敏感信息 |

## 事实与判断

- 推荐路径是 STS 临时凭证直传 OSS，再保存 `system_file` 记录。
- `OssServiceImpl` 从 `tenant_config` 读取 OSS/STs 配置，不在代码中写死真实密钥。
- `StorageController.saveSysStorage` 只保存文件元数据，不负责实际文件上传。
- `SystemFileManagerImpl.save` 会从 URL 解析 `endpoint` 和 `file_key`，降低 OSS 域名变更成本。
- 前端 `bz-upload` 组合租户、模块路径、日期、UUID 和原文件名生成 OSS key。
- 旧 `UploadController` 使用后端中转上传，服务端直接持有 OSS 长期凭据；可迁移但不建议作为新项目默认方案。
