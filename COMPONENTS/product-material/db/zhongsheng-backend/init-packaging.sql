CREATE TABLE IF NOT EXISTS `packaging`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `type_id` BIGINT NOT NULL COMMENT '包材类型ID（baseDataId）',
  `type_name` VARCHAR(128) NOT NULL COMMENT '包材类型名称快照',
  `name` VARCHAR(128) NOT NULL COMMENT '包材名称',
  `size` VARCHAR(64) DEFAULT NULL COMMENT '尺寸',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_packaging_type_id` (`type_id`),
  KEY `idx_packaging_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='包材表';
