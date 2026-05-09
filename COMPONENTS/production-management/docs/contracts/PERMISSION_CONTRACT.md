# Permission Contract

生产组件权限统一维护在 `ApiPermissionConstants` 和前端 `ZS_PERMISSIONS.production`。

| 权限 | 用途 |
| :--- | :--- |
| `production:group:page` | 生产组列表和下拉 |
| `production:group:detail` | 生产组详情 |
| `production:group:save` | 新增或编辑生产组 |
| `production:group:remove` | 删除生产组 |
| `production:order:page` | 生产总单列表 |
| `production:order:detail` | 生产总单详情 |
| `production:order:save` | 手工生产单保存和选品 |
| `production:order:progress` | 分批安排 |
| `production:order:delivery` | 产品行交货 |
| `production:order:export` | 生产单导出 |

公共单据日志、解锁、重新确认和负责人改派使用 `document:*` 权限。
