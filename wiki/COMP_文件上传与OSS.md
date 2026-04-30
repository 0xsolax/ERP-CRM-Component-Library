# COMP 文件上传与 OSS

## 基本信息

| 字段 | 内容 |
| :--- | :--- |
| 组件类型 | 基础设施 / 文件 |
| 复用等级 | 可参考改造 |
| 适用项目 | 产品图片、材料图片、客户附件、报价/订单文件 |
| 组件快照 | [COMPONENTS/file-oss](../COMPONENTS/file-oss/README.md) |
| 来源路径 | `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file`、`RAW/PROJECTs/zhongsheng-backend/docs/sql/init-system-file.sql`、`RAW/PROJECTs/qmy-admin/src/components/bz-upload`、`RAW/PROJECTs/qmy-admin/src/api/admin/system/storage.ts`、`RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/java/com/erp/controller/UploadController.java` |

## 组件快照

- [COMPONENTS/file-oss](../COMPONENTS/file-oss/README.md)
- [来源映射](../COMPONENTS/file-oss/SOURCE_MAP.md)
- [组件规范](../COMPONENTS/file-oss/docs/spec/COMPONENT_SPEC.md)
- [API 契约](../COMPONENTS/file-oss/docs/contracts/API_CONTRACT.md)
- [数据契约](../COMPONENTS/file-oss/docs/contracts/DATA_CONTRACT.md)
- [权限契约](../COMPONENTS/file-oss/docs/contracts/PERMISSION_CONTRACT.md)
- [验收清单](../COMPONENTS/file-oss/docs/acceptance/ACCEPTANCE.md)

## 业务目标

为产品、材料、客户附件、报价文件等提供统一上传、存储记录和访问 URL 管理能力，避免每个业务模块重复实现上传逻辑。

## 推荐链路

```text
前端上传组件
  -> GET /oss/getOssToken
  -> OSS STS 直传
  -> POST /storage/saveSysStorage
  -> system_file
  -> 业务表引用 system_file.id
```

## 前端入口

- 上传组件：`RAW/PROJECTs/qmy-admin/src/components/bz-upload`。
- 存储记录 API：`RAW/PROJECTs/qmy-admin/src/api/admin/system/storage.ts`，路径 `/storage/saveSysStorage`。

## 后端接口

| 能力 | 方法 | 路径 | 来源 |
| :--- | :--- | :--- | :--- |
| 获取 OSS STS | GET | `/oss/getOssToken` | `OssController` |
| 保存文件记录 | POST | `/storage/saveSysStorage` | `StorageController` |
| 旧图片上传 | POST | `/api/upload/image` | `UploadController` |

## 数据结构

| 表/对象 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `system_file` | `main_type`、`sub_type`、`master_id`、`url`、`endpoint`、`file_key`、`name`、`type`、`size` | 文件存储记录 |
| `tenant_config` | `tenant.oss.*` | OSS/STs 配置来源 |
| `StorageDTO` | `url`、`name`、`type`、`size` | 保存文件记录入参 |
| `OssStsTokenVO` | 临时凭证、bucket、endpoint、过期时间 | 前端直传可用 |

## 权限边界

- `GET /oss/getOssToken` 已绑定 `file:oss:token` 方法级权限。
- `POST /storage/saveSysStorage` 已绑定 `file:oss:save` 方法级权限。
- 上传接口必须要求登录，并确认方法级安全在目标应用中启用。
- 获取 STS token 的接口不应匿名开放。
- OSS 长期 AccessKey 不得暴露给前端。
- STS Policy 应限制租户、路径前缀、操作类型和有效期。
- 产品图片、客户附件、报价文件的访问范围应按业务模块控制。

## 接入步骤

1. 执行 `system_file` DDL。
2. 确认目标基座是否已有 `tenant_config` 或等价配置源。
3. 配置 OSS endpoint、bucket、STS role、duration 和 policy。
4. 接入 `/oss/getOssToken` 与 `/storage/saveSysStorage`。
5. 接入上传组件，并按业务设置 `modulePath`。
6. 上传成功后保存 `system_file` 记录。
7. 业务表优先保存 `system_file.id`；兼容历史时可同步保存 URL。

## 验收清单

- [ ] 已登录用户能获取 STS 临时凭证。
- [ ] 未登录用户不能获取 STS 临时凭证。
- [ ] 文件能直传 OSS 并返回可访问 URL。
- [ ] 文件记录能保存到 `system_file`。
- [ ] 业务表能引用文件 ID。
- [ ] 前端不包含 OSS 长期密钥。
- [ ] STS Policy 已限制租户路径和过期时间。
- [ ] 删除或替换文件有明确策略。

## 已知风险

- 旧 `UploadController` 直接使用后端长期凭据中转上传；新版更适合使用 STS 和文件记录。
- 业务表保存 URL 还是文件 ID 需要项目统一，否则后续迁移 OSS 域名会有成本。
- 文件记录复用必须限定 `main_type/sub_type/master_id/url` 业务域，不能只按 URL 全局去重。
- 当前快照未覆盖私有文件签名下载和 OSS 对象删除，需要后续按业务补齐。
