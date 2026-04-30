# REVIEW_EVIDENCE｜file-oss SOL-45

## 复核结论

SOL-45 review 中的 4 个代码层 blocker 已完成静态闭环。

本证据文件只记录当前组件快照内可复核的代码与文档证据。`COMPONENTS/file-oss/` 是多来源组件快照，不是独立 Spring Boot 工程，因此未伪造接口联调、数据库写入或真实 OSS 上传通过结论。

## 修复证据

### 1. `/oss/getOssToken` 鉴权

文件：

- `COMPONENTS/file-oss/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file/controller/OssController.java`

证据：

```java
@GetMapping("/getOssToken")
@PreAuthorize("@ss.hasPermission('file:oss:token')")
@Operation(summary = "获取OSS STS临时凭证")
public ResultInfo<OssStsTokenVO> getOssToken() {
    return ResultInfo.success(ossService.getOssToken());
}
```

结论：

- STS token 接口已从“文档建议鉴权”收口到 Controller 方法级权限。
- 接入目标项目时仍需确认 `@EnableMethodSecurity`、`SpElPermissionService` 和登录态拦截器生效。

### 2. `/storage/saveSysStorage` 鉴权

文件：

- `COMPONENTS/file-oss/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file/controller/StorageController.java`

证据：

```java
@PostMapping("/saveSysStorage")
@PreAuthorize("@ss.hasPermission('file:oss:save')")
@Operation(summary = "保存文件", description = "返回文件表 id 和 url，可用于进一步文件的保存")
public ResultInfo<StorageSaveVO> saveSysStorage(@RequestBody @Validated StorageDTO storageDTO) {
    ...
}
```

结论：

- 文件记录写入接口已具备可复核的方法级权限边界。

### 3. `saveOrUpdate` 同业务域复用

文件：

- `COMPONENTS/file-oss/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file/manager/impl/SystemFileManagerImpl.java`

证据：

```java
private SystemFileDO findExistingInSameScope(String mainCode, String subCode, SystemFileDO row) {
    LambdaQueryWrapper<SystemFileDO> query = Wrappers.<SystemFileDO>lambdaQuery()
            .eq(SystemFileDO::getMainType, mainCode)
            .eq(SystemFileDO::getSubType, subCode)
            .eq(SystemFileDO::getUrl, row.getUrl())
            .eq(SystemFileDO::getIsDeleted, 0);
    if (row.getMasterId() == null) {
        query.isNull(SystemFileDO::getMasterId);
    } else {
        query.eq(SystemFileDO::getMasterId, row.getMasterId());
    }
    return systemFileDAO.selectOne(query.last("LIMIT 1"));
}
```

结论：

- 不再按 URL 全局复用文件记录。
- 无 id 时只在 `mainType + subType + masterId + url + isDeleted=0` 同业务域内复用。
- 有 id 时按 id 更新，避免误覆盖其他业务对象。

### 4. 头像文件字段完整落库

文件：

- `COMPONENTS/file-oss/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file/manager/impl/SystemFileManagerImpl.java`

证据：

```java
private static void fillAvatarFileFields(SystemFileDO row, String url) {
    row.setUrl(url);
    row.setEndpoint(StrUtils.parseEndpoint(url));
    row.setFileKey(StrUtils.parseKey(url));
    row.setName(fileNameHintFromUrl(url));
}
```

结论：

- 头像新增和头像替换均会同步写入 `url`、`endpoint`、`fileKey`、`name`。
- 历史记录 URL 相同但 `endpoint/fileKey` 缺失时，会补齐字段后返回。

## 静态验证记录

### Diff 格式检查

```bash
git diff --check
# exit code 0
```

### 关键实现扫描

```bash
rg -n "PreAuthorize|file:oss|findExistingInSameScope|fillAvatarFileFields|normalizeScopedFileFields" COMPONENTS/file-oss/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/file
```

已确认：

- `OssController` 存在 `@PreAuthorize("@ss.hasPermission('file:oss:token')")`。
- `StorageController` 存在 `@PreAuthorize("@ss.hasPermission('file:oss:save')")`。
- `SystemFileManagerImpl` 存在同业务域复用查询。
- `SystemFileManagerImpl` 存在头像字段补齐逻辑。

### 工程形态检查

```bash
find COMPONENTS/file-oss -name pom.xml -o -name package.json
# 空输出
```

结论：

- 当前目录不是独立可编译工程，无法在组件快照内直接运行 Maven、前端构建或接口测试。

## 待目标项目装配后执行的验收

- 未登录请求 `/oss/getOssToken` 返回鉴权失败。
- 无 `file:oss:token` 权限请求 `/oss/getOssToken` 返回权限失败。
- 已登录且拥有 `file:oss:token` 权限可获取 STS 临时凭证。
- 未登录请求 `/storage/saveSysStorage` 返回鉴权失败。
- 已登录且拥有 `file:oss:save` 权限可保存 `{ id, url }` 文件记录。
- 同 URL、不同 `mainType/subType/masterId` 写入时不互相覆盖。
- 头像替换后 `url/endpoint/fileKey/name` 均更新为新值。

## 最小提交建议

本轮 SOL-45 建议只提交以下范围：

```text
COMPONENTS/file-oss/
wiki/COMP_文件上传与OSS.md
```

不要混入 `.obsidian/workspace.json`、`COMPONENTS/product-material/` 或 `RAW/PROJECTs/` 的无关改动。
