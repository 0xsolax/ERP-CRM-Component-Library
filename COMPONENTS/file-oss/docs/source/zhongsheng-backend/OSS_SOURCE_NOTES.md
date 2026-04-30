# zhongsheng-backend OSS 来源说明

## 相关来源

- `zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file`
- `zhongsheng-api/src/main/java/com/qmy/zhongsheng/api/dto/file`
- `zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/constants/TenantConfigCodeConstants.java`
- `zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/enums/TenantConfigCodeEnum.java`
- `zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/error/OssErrorCodeConstants.java`
- `zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/utils/StrUtils.java`
- `docs/sql/init-system-file.sql`
- `docs/sql/init-tenant.sql`

## 来源事实

- `/oss/getOssToken` 返回 OSS STS 临时凭证，前端直传 OSS 使用。
- `/storage/saveSysStorage` 保存文件记录，返回 `system_file.id` 和 `url`。
- `system_file` 同时保存 `url`、`endpoint`、`file_key`，用于降低 OSS 域名变更影响。
- OSS AccessKey、RoleArn、STS Policy 等配置来自 `tenant_config`，不得硬编码到代码或前端。

## 未复制内容

- 未复制完整项目 README，避免把认证、调试 token、默认 JWT 配置等与 file-oss 无关的信息带入组件快照。
