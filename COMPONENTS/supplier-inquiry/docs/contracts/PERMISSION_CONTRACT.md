# Permission Contract

| 权限 | 用途 |
| :--- | :--- |
| `supplier:inquiry:page` | 台账列表、历史查询 |
| `supplier:inquiry:detail` | 询价详情 |
| `supplier:inquiry:save` | 新增或编辑询价 |
| `supplier:inquiry:remove` | 删除询价 |
| `supplier:supplier:save` | 供应商快捷新增 |

菜单位于采购管理下。接口权限由后端 `@PreAuthorize` 最终拦截，前端按钮显隐只作为体验优化。
