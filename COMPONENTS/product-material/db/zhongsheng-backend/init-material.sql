-- 材料分类表
CREATE TABLE IF NOT EXISTS `material_category`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `name` VARCHAR(255) NOT NULL COMMENT '分类名称',
  `sort_num` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_material_category_sort_num` (`sort_num`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='材料分类表';

-- 材料表（材料管理 - 其他材料）
CREATE TABLE IF NOT EXISTS `material`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID（关联material_category.id）',
  `name` VARCHAR(255) NOT NULL COMMENT '材料名称',
  `size` VARCHAR(64) DEFAULT NULL COMMENT '尺寸',
  `price` DECIMAL(20, 2) DEFAULT NULL COMMENT '价格',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_material_category_id` (`category_id`),
  KEY `idx_material_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='材料表（其他材料：伞帽、伞珠、手柄、底座等）';