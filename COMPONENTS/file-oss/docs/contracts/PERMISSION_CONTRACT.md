# PERMISSION_CONTRACT｜file-oss

## 权限建议

| 权限码 | 适用接口 | 说明 |
| :--- | :--- | :--- |
| `file:oss:token` | `GET /oss/getOssToken` | 获取 STS 临时凭证，Controller 使用 `@PreAuthorize` 绑定 |
| `file:oss:save` | `POST /storage/saveSysStorage` | 保存文件记录，Controller 使用 `@PreAuthorize` 绑定 |
| `file:oss:view` | 文件列表或业务文件查询 | 查询文件记录 |
| `file:oss:remove` | 文件删除或解绑 | 删除文件记录或 OSS 对象 |

当前快照已在 `OssController` 和 `StorageController` 上定义方法级权限。新项目接入时仍需确认 `auth-permission` 的 `@EnableMethodSecurity`、`SpElPermissionService` 与登录态拦截器已纳入同一应用上下文。

## 安全边界

- 未登录用户不得获取 STS 临时凭证。
- 普通用户不得读取或修改 OSS 长期 AccessKey 配置。
- STS token 权限必须限制到当前租户和允许的路径前缀。
- 文件访问权限必须由业务模块决定，不能只依赖 URL 隐蔽性。
- 前端隐藏上传按钮不是安全边界，后端接口必须鉴权。

## 租户隔离

前端来源中上传路径形如：

```text
tenant_{tenantId}/{modulePath}/{yyyy}/{MM}/{dd}/{uuid}_{fileName}
```

接入时应确认：

- `tenantId` 来自可信登录态，不来自用户手输。
- `modulePath` 为白名单值。
- STS Policy 限制只能写入当前租户前缀。
- 文件名需要清理特殊字符或由服务端生成安全 key。

## 与 auth-permission 的关系

- 若沿用统一登录态，`GET /oss/getOssToken` 和 `POST /storage/saveSysStorage` 必须纳入认证拦截。
- 若引入方法级权限，建议复用 `auth-permission` 的权限集合与 SpEL 权限服务。
- 文件记录中的 `create_user`、`update_user` 应由登录上下文自动填充。
