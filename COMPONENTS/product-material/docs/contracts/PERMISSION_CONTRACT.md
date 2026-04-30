# PERMISSION_CONTRACT｜product-material

## 后端权限常量

| 常量 | 权限码 | 能力 |
| :--- | :--- | :--- |
| `PRODUCT_SAVE_OR_UPDATE` | `product:product:save` | 保存或更新产品 |
| `PRODUCT_PAGE` | `product:product:page` | 产品分页 |
| `PRODUCT_DETAIL` | `product:product:detail` | 产品详情 |
| `PRODUCT_DELETE` | `product:product:remove` | 删除产品 |
| `MATERIAL_CATEGORY_SAVE` | `material:category:save` | 保存材料分类 |
| `MATERIAL_CATEGORY_LIST` | `material:category:list` | 材料分类列表 |
| `MATERIAL_CATEGORY_DELETE` | `material:category:remove` | 删除材料分类 |
| `MATERIAL_SAVE_OR_UPDATE` | `material:material:save` | 保存或更新材料 |
| `MATERIAL_PAGE` | `material:material:page` | 材料分页 |
| `MATERIAL_DELETE` | `material:material:remove` | 删除材料 |
| `MATERIAL_LIST_BY_CATEGORY` | `material:material:list` | 按分类查询材料 |
| `FABRIC_SAVE_OR_UPDATE` | `material:fabric:save` | 保存或更新面料 |
| `FABRIC_PAGE` | `material:fabric:page` | 面料分页 |
| `FABRIC_LIST` | `material:fabric:list` | 面料列表 |
| `FABRIC_DETAIL` | `material:fabric:detail` | 面料详情 |
| `FABRIC_DELETE` | `material:fabric:remove` | 删除面料 |
| `UMBRELLA_FRAME_SAVE_OR_UPDATE` | `material:umbrella:save` | 保存或更新伞架 |
| `UMBRELLA_FRAME_PAGE` | `material:umbrella:page` | 伞架分页 |
| `UMBRELLA_FRAME_DETAIL` | `material:umbrella:detail` | 伞架详情 |
| `UMBRELLA_FRAME_DELETE` | `material:umbrella:remove` | 删除伞架 |
| `UMBRELLA_FRAME_LIST` | `material:umbrella:list` | 伞架列表 |
| `PACKAGING_SAVE_OR_UPDATE` | `material:packaging:save` | 保存或更新包材 |
| `PACKAGING_SAVE_DEFAULT` | `material:packaging:saveDefault` | 保存默认纸箱 |
| `PACKAGING_PAGE` | `material:packaging:page` | 包材分页 |
| `PACKAGING_DELETE` | `material:packaging:remove` | 删除包材 |
| `PROCESS_SAVE_OR_UPDATE` | `process:process:save` | 保存或更新工序 |
| `PROCESS_PAGE` | `process:process:page` | 工序分页 |
| `PROCESS_LIST` | `process:process:list` | 工序列表 |
| `PROCESS_DELETE` | `process:process:remove` | 删除工序 |

## 前端路由权限

| 路由 | 权限 |
| :--- | :--- |
| `/base-info/product/index` | `product:product:page` |
| `/base-info/product/add` | `product:product:save` |
| `/base-info/product/edit` | `product:product:save` |
| `/base-info/process` | `process:process:page` |
| `/material/umbrella-frame` | `material:umbrella:page` |
| `/material/fabric` | `material:fabric:page` |
| `/material/material` | `material:material:page` |
| `/material/packaging` | `material:packaging:page` |

## 权限接入规则

- 后端接口权限是最终边界，前端路由和按钮权限只做展示控制。
- 价格、成本、删除能力建议按角色拆分，不建议所有资料维护人员都能看成本。
- 若目标项目不使用 `@ss.perm(...)`，应建立字段名到目标权限码的映射表。
- 菜单权限和接口权限需要与 `system_menu.permission` 或目标权限表一致。
- 供应商价格、客户报价、历史利润属于后续采购/报价组件边界，不应混入产品主档公开权限。
