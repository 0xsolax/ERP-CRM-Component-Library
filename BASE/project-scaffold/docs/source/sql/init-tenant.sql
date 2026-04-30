CREATE TABLE IF NOT EXISTS `tenant_config` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `config_code` VARCHAR(255) NOT NULL COMMENT '配置 code',
  `config_name` VARCHAR(255) DEFAULT NULL COMMENT '配置名称',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `config_remark` VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_config_code_is_deleted` (`config_code`, `is_deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户配置表';
