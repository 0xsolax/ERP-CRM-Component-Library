# db｜product-material

## SQL 清单

| 文件 | 表 |
| :--- | :--- |
| `zhongsheng-backend/init-product.sql` | `product`、`product_type`、`product_umbrella_frame`、`product_material`、`product_fabric`、`product_printing`、`product_packaging`、`product_process_price` |
| `zhongsheng-backend/init-material.sql` | `material_category`、`material` |
| `zhongsheng-backend/init-fabric.sql` | `fabric` |
| `zhongsheng-backend/init-packaging.sql` | `packaging` |
| `zhongsheng-backend/init-process.sql` | `process` |
| `zhongsheng-backend/init-umbrella-frame.sql` | `umbrella_frame`、`umbrella_frame_material` |

## 依赖表

| 表 | 来源组件 | 用途 |
| :--- | :--- | :--- |
| `base_tree_node`、`base_data` | `base-data` | 产品类型、面料字段、包材类型、伞架规格、字段下拉 |
| `system_file` | `file-oss` | 产品、材料、伞架图片 |
| `system_menu`、权限表 | `auth-permission` | 菜单和接口权限 |

## 接入顺序

1. 先执行 `base-data` 的基础数据 SQL 和节点种子。
2. 再执行 `file-oss` 的 `system_file` SQL。
3. 执行本目录产品、材料、面料、包材、伞架、工序 SQL。
4. 按目标项目菜单权限模型写入产品、材料、工序权限。
5. 录入基础字段和初始物料数据后，再开放产品录入。
