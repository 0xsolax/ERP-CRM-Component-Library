# backend

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `zhongsheng-backend/` | 推荐方案：STS 临时凭证、文件记录、system_file 管理 |
| `zhongsheng-ai-legacy/` | 旧方案：后端中转上传到 OSS，仅作兼容对比 |

## 接入建议

- 新项目优先接入 `zhongsheng-backend` 方案。
- `OssController` 提供 `/oss/getOssToken`，前端拿临时凭证后直传 OSS。
- `StorageController` 提供 `/storage/saveSysStorage`，上传成功后保存文件记录。
- `SystemFileService` 可用于业务模块按 `mainType + subType + masterId` 保存和查询文件。
- 旧 `UploadController` 适合迁移历史单体项目，不建议作为新项目默认上传方式。

## 依赖注意

- 需要统一响应模型 `ResultInfo`。
- 需要统一异常模型与 `OssErrorCodeConstants`。
- 需要 `tenant_config` 或等价配置源提供 OSS/STs 配置。
- 需要登录鉴权或接口权限保护，不能匿名开放 STS token 获取接口。
