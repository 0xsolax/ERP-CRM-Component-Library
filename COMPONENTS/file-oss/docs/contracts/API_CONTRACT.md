# API_CONTRACT｜file-oss

## 后端接口

| 能力 | 方法 | 路径 | 入参 | 返回 | 建议权限 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 获取 OSS STS 临时凭证 | GET | `/oss/getOssToken` | 无 | `OssStsTokenVO` | `file:oss:token` |
| 保存文件记录 | POST | `/storage/saveSysStorage` | `StorageDTO` | `StorageSaveVO` | `file:oss:save` |
| 旧图片上传 | POST | `/api/upload/image` | `MultipartFile file` | `String url` | legacy |

## OssStsTokenVO

| 字段 | 说明 |
| :--- | :--- |
| `expiration` | 临时凭证过期时间 |
| `accessKeyId` | STS 临时 AccessKeyId |
| `accessKeySecret` | STS 临时 AccessKeySecret |
| `securityToken` | STS 安全令牌 |
| `requestId` | STS 请求 ID |
| `endpoint` | OSS endpoint |
| `bucketName` | OSS bucket |

说明：该对象返回的是 STS 临时凭证，不是 OSS 主账号长期密钥。有效期、权限范围和路径前缀必须由后端配置控制。

## StorageDTO

| 字段 | 说明 | 校验 |
| :--- | :--- | :--- |
| `name` | 文件名称 | 必填 |
| `url` | 文件访问 URL | 必填 |
| `type` | MIME 类型 | 可选 |
| `size` | 文件大小，字节 | 可选 |

## StorageSaveVO

| 字段 | 说明 |
| :--- | :--- |
| `id` | `system_file.id` |
| `url` | 文件访问 URL |

## 旧 UploadController

| 能力 | 说明 |
| :--- | :--- |
| 路径 | `POST /api/upload/image` |
| 入参 | `MultipartFile file` |
| 返回 | 可访问 URL |
| 用途 | 历史项目迁移对比，不作为新项目首选 |

## 前端 API

| 方法 | 路径 | 来源 |
| :--- | :--- | :--- |
| `saveSysStorage` | `/storage/saveSysStorage` | `frontend/qmy-admin/src/api/admin/system/storage.ts` |
| 上传组件 STS action | `/oss/getOssToken` | `frontend/qmy-admin/src/components/bz-upload/index.vue` |

## 错误语义

| 场景 | 期望 |
| :--- | :--- |
| OSS STS 配置缺失 | 返回 `OSS_STS_CONFIG_MISSING` |
| AssumeRole 调用失败 | 返回 `OSS_STS_TOKEN_ERROR`，日志记录服务端错误码和 requestId |
| 文件名称为空 | `StorageDTO.name` 校验失败 |
| 文件 URL 为空 | `StorageDTO.url` 校验失败 |
| 未登录获取 STS | 返回鉴权失败 |
