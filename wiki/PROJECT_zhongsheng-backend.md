# PROJECT zhongsheng-backend

## 定位

`zhongsheng-backend` 是中盛新版 Spring Boot 多模块后端。它对 ERP/CRM 组件库的主要价值是更规范的协议契约、权限注解、菜单权限、基础数据、产品物料、文件存储和租户配置实现。

## 来源路径

- `RAW/PROJECTs/zhongsheng-backend/README.md`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-api`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-core`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-common`
- `RAW/PROJECTs/zhongsheng-backend/zhongsheng-application`
- `RAW/PROJECTs/zhongsheng-backend/docs/sql`

## 已确认能力

- 登录认证、JWT、扫码登录策略、Token 状态。
- `@PreAuthorize("@ss.hasPermission(@ss.perm('...'))")` 权限门。
- `system_menu`、`role`、`role_menu`、`user_role` 菜单角色模型。
- 产品、伞架、面料、材料、包材、工序、基础数据。
- OSS STS 临时凭证和文件存储记录。
- 对外部系统的租户同步接口。

## 可贡献组件

| 组件 | 证据 | 复用价值 |
| :--- | :--- | :--- |
| 登录认证与权限 | `UserAuthController`、`SysUserController`、`RoleController`、`SystemMenuController`、`init-auth.sql`、`init-system-menu.sql` | 新项目权限基座 |
| 产品物料基础数据 | `ProductController`、`MaterialController`、`FabricController`、`UmbrellaFrameController`、`PackagingController`、SQL | 伞类产品与物料数据契约 |
| 文件上传与 OSS | `OssController`、`StorageController`、`init-system-file.sql` | 文件存储与 OSS 临时凭证 |
| 基础数据 | `BaseDataController`、`init-base-data.sql` | 字典/字段/分类等通用配置 |

## 待验证

- 当前新版后端未覆盖完整客户、报价、订单、采购、仓储闭环，这些仍需从 `zhongsheng-AI` 和 `qmy-admin` 提取。
- 前端接口路径与新版后端路径需要逐项对表，不能默认完全一致。

