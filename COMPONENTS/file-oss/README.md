# file-oss 文件上传与 OSS 组件

## 定位

`file-oss` 提供 ERP/CRM 后台通用文件能力：获取 OSS STS 临时凭证、前端直传 OSS、保存文件记录到 `system_file`，并为产品图片、材料图片、客户附件、报价/订单文件等业务模块提供统一引用方式。

本组件是可追溯快照，不是已打包 SDK。新项目应按目标基座、鉴权方式、OSS 服务商和业务表设计改造后接入。

## 复用等级

| 字段 | 内容 |
| :--- | :--- |
| 状态 | `reference` |
| 组件类型 | 基础组件 |
| 依赖组件 | `BASE/project-scaffold`、`auth-permission` |
| 主要来源 | `RAW/PROJECTs/zhongsheng-backend`、`RAW/PROJECTs/qmy-admin`、`RAW/PROJECTs/zhongsheng-AI` |

## 快照结构

| 目录 | 内容 |
| :--- | :--- |
| `backend/zhongsheng-backend/` | STS token、文件记录、system_file manager/service、DTO/VO、租户配置常量、OSS 错误码 |
| `backend/zhongsheng-ai-legacy/` | 旧后端中转上传 `UploadController`，仅作对比参考 |
| `frontend/qmy-admin/` | `bz-upload` 上传组件与 `saveSysStorage` API |
| `db/zhongsheng-backend/` | `system_file` 与 `tenant_config` DDL |
| `docs/source/` | 来源说明 |
| `docs/spec/` | 组件规范 |
| `docs/contracts/` | API、数据、权限契约 |
| `docs/acceptance/` | 快照与接入验收清单 |

## 推荐链路

1. 前端上传组件请求 `GET /oss/getOssToken`。
2. 后端从 `tenant_config` 读取 OSS/STs 配置，通过 AssumeRole 生成临时凭证。
3. 前端使用临时凭证直传 OSS，上传路径按租户、模块、日期和 UUID 生成。
4. 上传成功后，前端调用 `POST /storage/saveSysStorage` 保存文件记录。
5. 后端写入 `system_file`，拆分保存 `endpoint`、`file_key`、`url`、`name`、`type`、`size`。
6. 业务表优先保存 `system_file.id`；如必须保存 URL，应同步保留文件记录用于后续迁移和审计。

## 能力边界

已覆盖：

- OSS STS 临时凭证获取。
- 文件记录保存。
- `system_file` 文件元数据表。
- 文件主类型、次类型枚举。
- 业务文件批量保存与按业务主对象查询。
- 前端上传组件对接 STS 直传。
- 旧后端中转上传方案对比。

待项目确认：

- OSS 服务商是否固定为阿里云 OSS，或需要抽象到 S3/R2/MinIO。
- STS Policy 是否按租户、模块、文件类型限制上传路径和权限。
- 文件访问 URL 是否需要私有读、签名下载或 CDN 域名。
- 业务表统一保存 `storageId`，还是兼容历史 URL 字段。
- 删除文件时只逻辑删除记录，还是同时删除 OSS 对象。

## 快速接入

1. 执行 `db/zhongsheng-backend/init-system-file.sql`。
2. 若目标基座尚无租户配置表，执行或合并 `db/zhongsheng-backend/init-tenant.sql`。
3. 接入 `backend/zhongsheng-backend` 中的 DTO、controller、service、manager、dao、entity、VO 和枚举。
4. 配置 `tenant_config` 中的 OSS/STs 配置项，不得把 AccessKey 写进前端或代码常量。
5. 接入前端 `frontend/qmy-admin/src/components/bz-upload` 与 `storage.ts`。
6. 业务模块上传后保存 `system_file.id`，并按业务主对象维护 `mainType`、`subType`、`masterId`。
7. 按 `docs/acceptance/ACCEPTANCE.md` 做 STS、上传、文件记录、权限和安全验收。

## 安全规则

- 不得暴露 OSS 主账号 AccessKey 或长期 AccessKeySecret 到前端。
- 前端只能拿到 STS 临时凭证，且凭证必须有过期时间和最小权限 Policy。
- STS Policy 应限制 bucket、路径前缀、操作类型和有效时间。
- 上传路径必须避免用户可控路径穿越，文件名建议加 UUID 或服务端生成 key。
- 文件类型、大小、数量和可访问范围必须在业务层或上传组件层限制。
- 旧 `UploadController` 的后端中转方式仅作兼容参考，不作为新项目首选方案。
