# backend｜product-material

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `zhongsheng-backend/zhongsheng-core/.../core/product` | 产品主档与产品 BOM 后端 |
| `zhongsheng-backend/zhongsheng-core/.../core/material` | 伞架、面料、材料、材料分类、包材后端 |
| `zhongsheng-backend/zhongsheng-core/.../core/process` | 工序工价后端 |
| `zhongsheng-backend/zhongsheng-api/.../dto/product` | 产品 DTO |
| `zhongsheng-backend/zhongsheng-api/.../dto/material` | 材料 DTO |
| `zhongsheng-backend/zhongsheng-api/.../dto/process` | 工序 DTO |
| `zhongsheng-backend/zhongsheng-common/.../error` | 产品物料相关错误码 |
| `zhongsheng-backend/zhongsheng-common/.../ApiPermissionConstants.java` | 权限码来源 |

## 依赖

- `base-data`：`BaseDataManager`、`BaseTreeNodeManager`、`BaseDataDO`、基础数据节点和字段分类。
- `file-oss`：`SystemFileManager`、`SystemFileService`、`SystemFileDTO`、`SystemFileDO`、`FileVO`、文件枚举。
- `auth-permission`：`@PreAuthorize` 和 `@ss.perm(...)` 权限解析。
- 目标基座：统一响应、分页、异常、MyBatis Plus、租户/登录上下文等基础设施。

## 接入注意

- 本目录不是独立可编译模块，需要与依赖组件一起装配。
- `ProductServiceImpl` 的产品图片、`MaterialServiceImpl` 和 `UmbrellaFrameServiceImpl` 的图片保存依赖 `file-oss`。
- `PackagingServiceImpl.saveOrUpdateDefaultPaperBox` 依赖 `BaseTreeNodeSeedEnum.PACKAGING_TYPE` 和基础数据中“纸箱”类型。
- `FabricController` 来源路径为 `/fabric/deteil`；快照已新增 `/fabric/detail` 标准路径并保留旧路径兼容。
- 来源包材前端曾有 `/packaging/typeList` 和 `/box-price/list` 未使用封装；快照已移除，避免保留无后端 controller 的 API。
