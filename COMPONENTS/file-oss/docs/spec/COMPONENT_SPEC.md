# COMPONENT_SPEC｜file-oss

## 目标

为 ERP/CRM 后台提供统一文件上传和文件记录能力，使产品图片、材料图片、客户附件、报价/订单文件等业务模块可以复用同一套上传、存储、引用和验收规则。

## 推荐架构

```text
前端上传组件
  -> GET /oss/getOssToken
  -> OSS STS 直传
  -> POST /storage/saveSysStorage
  -> system_file
  -> 业务表引用 system_file.id
```

## 后端规范

### STS token

- 接口：`GET /oss/getOssToken`。
- 配置来源：`tenant_config`。
- 必需配置：
  - `tenant.oss.endpoint`
  - `tenant.oss.bucket-name`
  - `tenant.oss.access-key-id`
  - `tenant.oss.access-key-secret`
  - `tenant.oss.sts-region-id`
  - `tenant.oss.sts-endpoint`
  - `tenant.oss.sts-role-arn`
  - `tenant.oss.sts-role-session-name`
  - `tenant.oss.sts-duration-seconds`
  - `tenant.oss.sts-policy`
- 返回给前端的是临时凭证，不得返回长期 AccessKey。

### 文件记录

- 接口：`POST /storage/saveSysStorage`。
- 入参：`StorageDTO`，包含 `name`、`url`、`type`、`size`。
- 返回：`StorageSaveVO`，包含 `id` 和 `url`。
- 服务端应拆分保存：
  - `endpoint`
  - `file_key`
  - `url`
  - `name`
  - `type`
  - `size`

### 业务挂载

- 业务模块通过 `main_type + sub_type + master_id` 查询或保存文件。
- `main_type` 表示业务主域，例如 `MATERIAL`、`PRODUCT`。
- `sub_type` 表示业务文件类型，例如 `MATERIAL_IMAGE`、`PRODUCT_IMAGE`。
- `master_id` 关联具体业务主对象。

## 前端规范

- 上传组件必须先获取 STS 临时凭证。
- 上传 key 应包含租户、业务模块、日期和随机 UUID。
- 上传成功后必须保存 `system_file` 记录。
- 对业务页面回传 `{ id, url }`。
- 不应在组件内硬编码长期 OSS 密钥。

## 新项目推荐保存策略

| 场景 | 推荐保存 |
| :--- | :--- |
| 新业务表 | 保存 `system_file.id` |
| 兼容历史表 | 可暂存 `url`，同时保存 `system_file.id` |
| 多文件附件 | 建议业务文件关联表或使用 `main_type + sub_type + master_id` |
| 公开图片 | 可保存 URL 缓存展示，但源记录仍以 `system_file` 为准 |
| 私有附件 | 保存文件 ID，通过后端签名下载或权限校验后返回临时 URL |

## 旧方案对比

`zhongsheng-AI` 的 `UploadController` 是后端中转上传：

- 优点：接入简单，前端不接触 STS。
- 缺点：后端承载上传流量，长期 OSS 凭据在服务端配置中直接参与上传，缺少统一 `system_file` 记录。
- 结论：可作为迁移参考，不作为新项目默认方案。

## 已知缺口

- 快照不是独立可编译模块，依赖目标基座的响应模型、异常模型、登录拦截器和 MyBatis 基础设施。
- `bz-upload` 仍包含多项目 `pageType` 分支，新项目应按自身前端基座收敛。
- 当前快照未实现 OSS 对象删除或私有签名下载，接入时需按业务补齐。
