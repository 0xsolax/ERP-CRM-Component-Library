# PERMISSION_CONTRACT｜base-data

## 后端权限

中圣版 controller 使用方法级权限：

```java
@PreAuthorize("@ss.hasPermission(@ss.perm('BASE_DATA_SAVE_OR_UPDATE'))")
```

权限常量来源：

```text
backend/zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/constants/ApiPermissionConstants.java
```

| 常量 | 权限码 | 适用接口 |
| :--- | :--- | :--- |
| `BASE_DATA_SAVE_OR_UPDATE` | `base:data:save` | `/baseData/saveOrUpdate` |
| `BASE_DATA_DELETE` | `base:data:remove` | `/baseData/delete` |
| `BASE_DATA_LIST` | `base:data:list` | `/baseData/list` |
| `BASE_DATA_LIST_BY_NODE_KEY` | `base:data:query` | `/baseData/listByNodeKey` |
| `BASE_DATA_TREE_NODE_LIST` | `base:data:tree` | `/baseData/treeNodeList` |

## 前端权限

来源路由：

```text
frontend/qmy-admin/src/views/zs/router/async-modules/base-info.ts
```

当前来源路由中字段管理使用 `permission: 'sys:role:list'`，这是占位或复用旧权限，不应直接作为新项目权限。

建议目标权限：

| 前端入口 | 建议权限 |
| :--- | :--- |
| 字段管理页面可见 | `base:data:list` 或 `base:data:tree` |
| 新增/编辑基础数据 | `base:data:save` |
| 删除基础数据 | `base:data:remove` |
| 下拉查询 | `base:data:query` |

## 权限边界

- 页面可见性由前端路由控制，但不是安全边界。
- 新增、编辑、删除必须由后端接口权限控制。
- 下拉查询是否需要登录用户权限，应按业务敏感度决定；内部 ERP 默认需要登录。
- 字段管理通常是后台配置能力，不应开放给普通业务录入用户。

## 与 auth-permission 的关系

- 若沿用中圣版 `@ss.hasPermission`，必须先接入 `auth-permission` 中的当前用户、权限集合和 SpEL 权限服务。
- 若目标项目采用其他权限框架，应保留 `base:data:*` 语义并替换注解实现。
