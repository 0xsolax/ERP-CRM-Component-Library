-- 中圣公共单据状态、动作日志与锁定初始化脚本
-- 适用模块：MIG-06 公共单据状态、动作日志与锁定

CREATE TABLE IF NOT EXISTS `document_action_log`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `document_type` VARCHAR(32) NOT NULL COMMENT '单据类型：quote/order/purchase/production',
  `document_id` BIGINT NOT NULL COMMENT '单据 ID',
  `base_code` VARCHAR(64) DEFAULT NULL COMMENT '业务主单号',
  `serial_code` VARCHAR(64) DEFAULT NULL COMMENT '序列号',
  `action_type` VARCHAR(64) NOT NULL COMMENT '动作类型',
  `before_status` VARCHAR(32) DEFAULT NULL COMMENT '动作前状态',
  `after_status` VARCHAR(32) DEFAULT NULL COMMENT '动作后状态',
  `before_lock_state` VARCHAR(32) DEFAULT NULL COMMENT '动作前锁定状态',
  `after_lock_state` VARCHAR(32) DEFAULT NULL COMMENT '动作后锁定状态',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作者 ID',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作者名称',
  `action_reason` VARCHAR(1000) DEFAULT NULL COMMENT '动作原因',
  `diff_summary` VARCHAR(500) DEFAULT NULL COMMENT '差异摘要',
  `diff_detail` LONGTEXT COMMENT '字段级差异明细',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_action_doc` (`document_type`, `document_id`, `is_deleted`),
  KEY `idx_document_action_time` (`create_time`, `is_deleted`),
  KEY `idx_document_action_operator` (`operator_id`, `create_time`, `is_deleted`),
  KEY `idx_document_action_action` (`action_type`, `create_time`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公共单据动作日志';

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `document_action_log` ADD KEY `idx_document_action_operator` (`operator_id`, `create_time`, `is_deleted`)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'document_action_log' AND INDEX_NAME = 'idx_document_action_operator'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `document_action_log` ADD KEY `idx_document_action_action` (`action_type`, `create_time`, `is_deleted`)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'document_action_log' AND INDEX_NAME = 'idx_document_action_action'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `document_unlock_request`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `document_type` VARCHAR(32) NOT NULL COMMENT '单据类型：quote/order/purchase/production',
  `document_id` BIGINT NOT NULL COMMENT '单据 ID',
  `base_code` VARCHAR(64) DEFAULT NULL COMMENT '业务主单号',
  `serial_code` VARCHAR(64) DEFAULT NULL COMMENT '序列号',
  `request_type` VARCHAR(32) NOT NULL COMMENT '申请类型：request/warning',
  `requester_id` BIGINT DEFAULT NULL COMMENT '申请人 ID',
  `requester_name` VARCHAR(100) DEFAULT NULL COMMENT '申请人名称',
  `approver_role_key` VARCHAR(64) DEFAULT NULL COMMENT '审批角色标识',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人 ID',
  `approver_name` VARCHAR(100) DEFAULT NULL COMMENT '审批人名称',
  `request_reason` VARCHAR(1000) NOT NULL COMMENT '申请原因',
  `request_status` VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
  `decision_remark` VARCHAR(1000) DEFAULT NULL COMMENT '审批意见',
  `processed_at` DATETIME DEFAULT NULL COMMENT '处理时间',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_unlock_doc` (`document_type`, `document_id`, `request_status`, `is_deleted`),
  KEY `idx_document_unlock_requester` (`requester_id`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公共单据解锁申请';

-- 既有库可能已经创建过 orders 表；这里补齐 SOL-58 引入、SOL-59 依赖的订单快照与转单幂等字段。
SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `customer_contact` VARCHAR(100) DEFAULT NULL COMMENT ''客户联系人快照'' AFTER `customer_name`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'customer_contact'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `customer_phone` VARCHAR(100) DEFAULT NULL COMMENT ''客户电话快照'' AFTER `customer_contact`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'customer_phone'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `customer_email` VARCHAR(200) DEFAULT NULL COMMENT ''客户邮箱快照'' AFTER `customer_phone`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'customer_email'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `customer_address` VARCHAR(500) DEFAULT NULL COMMENT ''客户地址快照'' AFTER `customer_email`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'customer_address'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `active_quote_id` BIGINT GENERATED ALWAYS AS (CASE WHEN `is_deleted` = 0 THEN `quote_id` ELSE NULL END) STORED COMMENT ''非删除订单来源报价唯一键'' AFTER `deleted_time`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'active_quote_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD UNIQUE KEY `uk_orders_active_quote` (`active_quote_id`)',
    'SELECT 1'
  )
  FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND INDEX_NAME = 'uk_orders_active_quote'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `quote` ADD COLUMN `needs_reconfirm` TINYINT NOT NULL DEFAULT 0 COMMENT ''是否需要重新确认'' AFTER `lock_state`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quote' AND COLUMN_NAME = 'needs_reconfirm'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `quote` ADD COLUMN `reconfirm_scope_json` LONGTEXT COMMENT ''重新确认范围 JSON'' AFTER `needs_reconfirm`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quote' AND COLUMN_NAME = 'reconfirm_scope_json'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `needs_reconfirm` TINYINT NOT NULL DEFAULT 0 COMMENT ''是否需要重新确认'' AFTER `lock_state`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'needs_reconfirm'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE `orders` ADD COLUMN `reconfirm_scope_json` LONGTEXT COMMENT ''重新确认范围 JSON'' AFTER `needs_reconfirm`',
    'SELECT 1'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND COLUMN_NAME = 'reconfirm_scope_json'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
