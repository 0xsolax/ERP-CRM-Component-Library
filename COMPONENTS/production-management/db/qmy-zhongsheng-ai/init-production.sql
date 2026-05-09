-- 中圣生产履约初始化脚本
-- 适用模块：MIG-08 生产组、生产总单与交货进度

CREATE TABLE IF NOT EXISTS `production_group`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `code` VARCHAR(64) NOT NULL COMMENT '生产组编码',
  `name` VARCHAR(120) NOT NULL COMMENT '生产组名称',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用/0停用',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  `active_code` VARCHAR(64) GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN `code` ELSE NULL END
  ) STORED COMMENT '启用生产组编码唯一辅助列',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_production_group_active_code` (`active_code`),
  KEY `idx_production_group_status` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产组主档';

CREATE TABLE IF NOT EXISTS `production_order`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `code` VARCHAR(64) NOT NULL COMMENT '生产总单号',
  `base_code` VARCHAR(64) DEFAULT NULL COMMENT '业务主单号',
  `serial_code` VARCHAR(64) DEFAULT NULL COMMENT '序列号',
  `order_type` VARCHAR(32) NOT NULL DEFAULT 'master' COMMENT '生产单类型：master订单总单',
  `master_order_key` VARCHAR(120) DEFAULT NULL COMMENT '订单唯一生产总单键',
  `order_id` BIGINT DEFAULT NULL COMMENT '来源订单 ID',
  `order_code` VARCHAR(64) DEFAULT NULL COMMENT '来源订单号',
  `customer_id` BIGINT DEFAULT NULL COMMENT '客户 ID',
  `customer_name` VARCHAR(200) DEFAULT NULL COMMENT '客户名称快照',
  `delivery_date` DATE DEFAULT NULL COMMENT '订单交货日期',
  `status` VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT '状态：draft/in_production/completed/manual_reconcile',
  `lock_state` VARCHAR(32) NOT NULL DEFAULT 'open' COMMENT '锁定状态：open/locked/pending_unlock/temporary_unlocked',
  `needs_reconfirm` TINYINT NOT NULL DEFAULT 0 COMMENT '是否需要重新确认',
  `reconfirm_scope_json` LONGTEXT COMMENT '重新确认范围 JSON',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `owner_name` VARCHAR(100) DEFAULT NULL COMMENT '负责人名称',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  `active_code` VARCHAR(64) GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN `code` ELSE NULL END
  ) STORED COMMENT '启用生产单号唯一辅助列',
  `active_master_order_key` VARCHAR(120) GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 AND `order_type` = 'master' THEN `master_order_key` ELSE NULL END
  ) STORED COMMENT '启用订单唯一生产总单辅助列',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_production_order_active_master_key` (`active_master_order_key`),
  UNIQUE KEY `uk_production_order_serial_deleted` (`serial_code`, `is_deleted`, `deleted_time`),
  KEY `idx_production_order_code` (`code`, `is_deleted`),
  KEY `idx_production_order_base_code` (`base_code`, `is_deleted`),
  KEY `idx_production_order_order` (`order_id`, `is_deleted`),
  KEY `idx_production_order_status` (`status`, `is_deleted`),
  KEY `idx_production_order_owner` (`owner_id`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产总单主表';

-- 统一业务主单号后，同一订单可能拆出多张生产单；code/base_code 可复用，serial_code 才是内部唯一流水。
SET @sql = (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE `production_order` DROP INDEX `uk_production_order_active_code`',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'production_order'
    AND INDEX_NAME = 'uk_production_order_active_code'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `production_order_progress`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `production_order_id` BIGINT NOT NULL COMMENT '生产总单 ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '来源订单 ID',
  `order_code` VARCHAR(64) DEFAULT NULL COMMENT '来源订单号',
  `line_key` VARCHAR(120) NOT NULL COMMENT '订单产品行键',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品 ID',
  `product_code` VARCHAR(64) DEFAULT NULL COMMENT '产品编号',
  `product_name` VARCHAR(200) DEFAULT NULL COMMENT '产品名称快照',
  `order_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '订单数量',
  `planned_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '已安排生产数量',
  `purchased_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '采购覆盖数量',
  `inbound_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '采购入库释放数量',
  `produced_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '已生产数量',
  `delivered_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '累计已交货数量',
  `source_snapshot_json` LONGTEXT COMMENT '来源订单产品快照 JSON',
  `progress_status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '进度状态：pending/released/scheduled/delivering/completed/manual_reconcile',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_production_progress_line` (`production_order_id`, `line_key`, `is_deleted`),
  KEY `idx_production_progress_order` (`order_id`, `is_deleted`),
  KEY `idx_production_progress_status` (`progress_status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产总单产品行进度';

CREATE TABLE IF NOT EXISTS `production_order_batch`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `production_order_id` BIGINT NOT NULL COMMENT '生产总单 ID',
  `progress_id` BIGINT NOT NULL COMMENT '生产产品行进度 ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '来源订单 ID',
  `order_code` VARCHAR(64) DEFAULT NULL COMMENT '来源订单号',
  `line_key` VARCHAR(120) NOT NULL COMMENT '订单产品行键',
  `product_id` BIGINT DEFAULT NULL COMMENT '产品 ID',
  `product_code` VARCHAR(64) DEFAULT NULL COMMENT '产品编号',
  `product_name` VARCHAR(200) DEFAULT NULL COMMENT '产品名称快照',
  `production_group_id` BIGINT NOT NULL COMMENT '生产组 ID',
  `production_group_name` VARCHAR(120) NOT NULL COMMENT '生产组名称快照',
  `batch_qty` DECIMAL(18,4) NOT NULL DEFAULT 0.0000 COMMENT '本批安排数量',
  `planned_delivery_date` DATE DEFAULT NULL COMMENT '计划交期',
  `status` VARCHAR(32) NOT NULL DEFAULT 'scheduled' COMMENT '状态：scheduled/completed/cancelled',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_production_batch_order` (`production_order_id`, `is_deleted`),
  KEY `idx_production_batch_progress` (`progress_id`, `is_deleted`),
  KEY `idx_production_batch_group` (`production_group_id`, `is_deleted`),
  KEY `idx_production_batch_source_order` (`order_id`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产总单分批安排';
