# DATA_CONTRACT｜product-material

## 主表

| 表 | 说明 | 关键字段 |
| :--- | :--- | :--- |
| `product` | 产品主档 | `product_code`、`gross_weight`、`net_weight`、`loss_fee`、`description_zh`、`description_en`、`volume`、`small_cabinet`、`large_cabinet`、`total_cost`、`selling_price` |
| `material_category` | 材料分类 | `name`、`sort_num`、`remark` |
| `material` | 其他材料 | `category_id`、`name`、`size`、`price` |
| `fabric` | 面料 | `type_id/name`、`model_id/name`、`width_id/name`、`price`、`unit` |
| `packaging` | 包材 | `type_id/name`、`name`、`size`、`price` |
| `process` | 工序 | `name` |
| `umbrella_frame` | 伞架 | `function_id/name`、`type_id/name`、`length_id/name`、`diameter_id/name`、`rib_count_id/name`、`material_id/name`、`specific_attribute`、`price`、`unit` |

## 产品 BOM 表

| 表 | 说明 | 关键字段 |
| :--- | :--- | :--- |
| `product_type` | 产品类型关联 | `product_id`、`type_id`、`type_name` |
| `product_umbrella_frame` | 产品伞架 | `product_id`、`umbrella_frame_id`、规格 ID/名称快照、`price`、`unit`、`quantity` |
| `product_material` | 产品材料 | `product_id`、`material_id`、`category_id/name`、`name`、`size`、`quantity`、`price`、`is_bound` |
| `product_fabric` | 产品面料 | `product_id`、`fabric_id`、`type/model/width` ID 与名称、`price`、`unit`、`usage` |
| `product_printing` | 产品印刷 | `fabric_type_name`、`print_type_id/name`、`alignment_type_id/name`、`price`、`plate_fee` |
| `product_packaging` | 产品包材 | `product_id`、`packaging_id`、`type_id/name`、`name`、`size`、`box_count`、`price` |
| `product_process_price` | 产品工序工价 | `product_id`、`process_id`、`name`、`price` |
| `umbrella_frame_material` | 伞架材料绑定 | `umbrella_frame_id`、`material_category_id/name`、`material_id/name`、`quantity`、`size`、`price` |

## 基础数据依赖

| 业务字段 | 来源 |
| :--- | :--- |
| 产品类型 | `base_data` 产品类型节点 |
| 面料种类、型号、门幅 | `base_data` 面料节点 |
| 包材类型 | `base_data` 包材节点 |
| 伞架功能、类型、长度、中棒直径、骨数、材料 | `base_data` 伞架节点 |
| 印刷方式、对齐方式 | `base_data` 产品或印刷相关节点 |

## 文件依赖

| 业务 | 文件主类型 | 文件子类型 | 关联 |
| :--- | :--- | :--- | :--- |
| 产品图片 | `PRODUCT` | `PRODUCT_IMAGE` | `system_file.master_id = product.id` |
| 材料图片 | `MATERIAL` | `MATERIAL_IMAGE` | `system_file.master_id = material.id` |
| 伞架图片 | `MATERIAL` | `UMBRELLA_FRAME_IMAGE` | `system_file.master_id = umbrella_frame.id` |

## 成本字段边界

| 字段 | 所在位置 | 含义 |
| :--- | :--- | :--- |
| `product.total_cost` | 产品主档 | 产品默认总成本参考值 |
| `product.selling_price` | 产品主档 | 产品默认售价参考值 |
| `price` | 各物料和 BOM 子表 | 物料、包材、工序或组成项的参考单价 |
| `loss_fee` | 产品主档 | 损耗或杂费参考值 |
| `plate_fee` | `product_printing` | 印刷版费 |

报价单应另行保存报价时点的产品组成、数量、利润、折扣、汇率、税费、物流和客户敏感信息，不应只引用产品主档实时值。
