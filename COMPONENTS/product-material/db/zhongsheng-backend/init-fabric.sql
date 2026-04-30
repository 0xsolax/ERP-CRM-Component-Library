-- auto-generated definition
CREATE TABLE IF NOT EXISTS `fabric`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `type_id` BIGINT NOT NULL COMMENT '种类ID（baseDataId）',
  `type_name` VARCHAR(256) NOT NULL COMMENT '种类名称',
  `model_id` BIGINT NOT NULL COMMENT '型号ID（baseDataId）',
  `model_name` VARCHAR(256) NOT NULL COMMENT '型号',
  `width_id` BIGINT NOT NULL COMMENT '门幅ID（baseDataId）',
  `width_name` VARCHAR(256) NOT NULL COMMENT '门幅名称',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `unit` VARCHAR(10) NOT NULL COMMENT '单位：米或码',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_fabric_type_id` (`type_id`),
  KEY `idx_fabric_model_id` (`model_id`),
  KEY `idx_fabric_width_id` (`width_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面料表';
