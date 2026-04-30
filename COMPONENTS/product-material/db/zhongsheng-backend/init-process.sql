-- 工序表（生产工序管理）
CREATE TABLE IF NOT EXISTS `process`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `name` VARCHAR(64) NOT NULL COMMENT '工序名称',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_process_name_is_deleted` (`name`, `is_deleted`, `deleted_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='工序表';