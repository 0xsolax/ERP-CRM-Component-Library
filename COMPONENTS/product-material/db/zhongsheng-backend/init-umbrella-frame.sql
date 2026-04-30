-- 伞架表（材料管理 - 伞架）
-- auto-generated definition
CREATE TABLE IF NOT EXISTS `umbrella_frame`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `function_id` BIGINT NOT NULL COMMENT '功能ID（baseDataId）',
  `function_name` VARCHAR(256) DEFAULT NULL COMMENT '功能名称快照',
  `type_id` BIGINT NOT NULL COMMENT '类型ID（baseDataId）',
  `type_name` VARCHAR(256) DEFAULT NULL COMMENT '类型名称快照',
  `length_id` BIGINT NOT NULL COMMENT '伞架长度ID（baseDataId）',
  `length_name` VARCHAR(256) DEFAULT NULL COMMENT '伞架长度名称快照',
  `diameter_id` BIGINT NOT NULL COMMENT '中棒直径ID（baseDataId）',
  `diameter_name` VARCHAR(256) DEFAULT NULL COMMENT '中棒直径名称快照',
  `rib_count_id` BIGINT NOT NULL COMMENT '伞骨数量ID（baseDataId）',
  `rib_count_name` VARCHAR(256) DEFAULT NULL COMMENT '伞骨数量名称快照',
  `material_id` BIGINT NOT NULL COMMENT '材料ID（baseDataId）',
  `material_name` VARCHAR(256) DEFAULT NULL COMMENT '材料名称快照',
  `specific_attribute` VARCHAR(255) DEFAULT NULL COMMENT '特定属性',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `unit` VARCHAR(10) NOT NULL COMMENT '单位：支或打',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_umbrella_frame_function_id` (`function_id`),
  KEY `idx_umbrella_frame_type_id` (`type_id`),
  KEY `idx_umbrella_frame_length_id` (`length_id`),
  KEY `idx_umbrella_frame_diameter_id` (`diameter_id`),
  KEY `idx_umbrella_frame_rib_count_id` (`rib_count_id`),
  KEY `idx_umbrella_frame_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伞架表';

CREATE TABLE IF NOT EXISTS `umbrella_frame_material`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `umbrella_frame_id` BIGINT NOT NULL COMMENT '伞架ID',
  `material_category_id` BIGINT NOT NULL COMMENT '材料分类id',
  `material_category_name` VARCHAR(256) NOT NULL COMMENT '材料分类名称',
  `material_id` BIGINT NOT NULL COMMENT '材料ID（关联fabric或packaging表）',
  `material_name` VARCHAR(256) DEFAULT NULL COMMENT '材料名称',
  `quantity` INT NOT NULL COMMENT '数量',
  `size` VARCHAR(64) DEFAULT NULL COMMENT '尺寸（材料快照）',
  `price` DECIMAL(20, 2) DEFAULT NULL COMMENT '价格（材料快照）',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_umbrella_frame_material_umbrella_frame_id` (`umbrella_frame_id`),
  KEY `idx_umbrella_frame_material_material_type` (`material_category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伞架材料绑定表';
