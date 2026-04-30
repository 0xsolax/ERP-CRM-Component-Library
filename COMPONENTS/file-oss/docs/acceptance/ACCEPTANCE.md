# ACCEPTANCE｜file-oss

## 快照验收

- [x] `backend/zhongsheng-backend/` 包含 OSS STS、文件记录、DTO、VO、manager、service、dao、entity、枚举和支持常量。
- [x] `backend/zhongsheng-ai-legacy/` 包含旧 `UploadController` 对比快照。
- [x] `frontend/qmy-admin/` 包含 `bz-upload` 上传组件与 `storage.ts`。
- [x] `db/` 包含 `system_file` 与 `tenant_config` DDL。
- [x] `docs/contracts/` 覆盖 API、数据、权限契约。
- [x] 不包含 `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/`。
- [x] 不包含真实 OSS AccessKey、token、数据库连接或本地环境配置。

## 接入验收

- [ ] 未登录用户调用 `/oss/getOssToken` 被拒绝。
- [ ] 已登录用户调用 `/oss/getOssToken` 返回 STS 临时凭证、bucket、endpoint 和过期时间。
- [ ] OSS/STs 配置缺失时返回明确业务错误，不返回空凭证。
- [ ] 前端上传组件能使用 STS 直传 OSS。
- [ ] 上传成功后调用 `/storage/saveSysStorage` 保存文件记录。
- [ ] `system_file` 正确保存 `url`、`endpoint`、`file_key`、`name`、`type`、`size`。
- [ ] 业务页面收到 `{ id, url }`，并优先保存 `id`。
- [ ] 文件大小、类型、数量限制生效。
- [ ] 文件替换和删除策略明确。
- [ ] OSS 对象访问权限符合业务要求，私有文件不能被越权访问。

## 安全验收

- [ ] 前端源码不包含 OSS 长期 AccessKey 或 AccessKeySecret。
- [ ] Git 仓库不包含真实 OSS 密钥、数据库密码、token。
- [ ] STS Policy 限制 bucket、路径前缀、操作类型和有效期。
- [ ] 上传路径不能被用户构造为跨租户或路径穿越。
- [ ] 文件名特殊字符、同名文件、超长文件名有处理策略。

## 当前快照未运行验证

- 未对抽取快照做编译，因为它是多来源拼装证据包，不是单一工程。
- 未执行真实 OSS 上传，因为需要目标项目 OSS/STs 配置和测试 bucket。
- 未执行浏览器上传流程，因为 SOL-45 目标是组件快照抽取。
