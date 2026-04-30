# frontend

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `qmy-admin/src/components/bz-upload` | 上传组件封装 |
| `qmy-admin/src/api/admin/system/storage.ts` | 保存文件记录 API |

## 上传流程

1. 组件根据当前 `pageType` 选择后端 API 前缀。
2. 调用 `/oss/getOssToken` 获取 STS 临时凭证。
3. 根据租户、模块、日期、UUID 和原文件名生成 OSS key。
4. 使用 STS 凭证直传 OSS。
5. 上传成功后调用 `/storage/saveSysStorage` 保存文件记录。
6. 对外回调 `res.data = { id, url }`，业务页面可保存 `id` 或 `url`。

## 接入注意

- `modulePath` 应按业务模块设置，例如 `product/image`、`material/image`、`quote/attachment`。
- 新项目应收敛多租户 `pageType` 分支，避免保留不需要的历史项目逻辑。
- 文件类型、大小和数量限制应在业务页面或上传组件 props 中明确。
- 不得在前端写入长期 OSS AccessKey。
