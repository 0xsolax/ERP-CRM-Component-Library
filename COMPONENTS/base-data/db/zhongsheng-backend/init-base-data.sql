-- 基础树节点（字段管理、分类等共用；业务侧通过 node_id 关联 base_data）
CREATE TABLE IF NOT EXISTS `base_tree_node`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `biz_type`  VARCHAR(32)  NOT NULL COMMENT '业务类型（大写英文）：FIELD_MGMT 字段管理；CATEGORY 分类；FABRIC 面料；PACKAGING 包材；UMBRELLA_FRAME 伞架',
  `parent_id` BIGINT       NOT NULL DEFAULT 0 COMMENT '父节点 id，根节点为 0',
  `name`      VARCHAR(255) NOT NULL COMMENT '节点名称',
  `level`     INT          NOT NULL COMMENT '层级（根为 1）',
  `sort_num`  INT          NOT NULL DEFAULT 0 COMMENT '同级排序',
  `node_key`  VARCHAR(64)  DEFAULT NULL COMMENT '节点唯一标识，用于前后端交互（如：FABRIC_TYPE、UMBRELLA_FRAME_FUNCTION）',
  `data_bind_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '是否允许绑定 base_data：0 不允许，1 允许',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_base_tree_node_biz_parent_name_is_deleted` (`biz_type`, `parent_id`, `name`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_base_tree_node_node_key` (`node_key`),
  KEY `idx_base_tree_node_parent_id` (`parent_id`),
  KEY `idx_base_tree_node_biz_type` (`biz_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='基础树节点表';

CREATE TABLE IF NOT EXISTS `base_data` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `node_id` BIGINT NOT NULL COMMENT '基础树节点 id，关联 base_tree_node.id',
  `value1` VARCHAR(512) DEFAULT NULL COMMENT '值1',
  `value2` VARCHAR(512) DEFAULT NULL COMMENT '值2',
  `value3` VARCHAR(512) DEFAULT NULL COMMENT '值3',
  `value4` VARCHAR(512) DEFAULT NULL COMMENT '值4',
  `remark`  VARCHAR(1024) DEFAULT NULL COMMENT '备注',
  `ext_json` TEXT DEFAULT NULL COMMENT '扩展 JSON',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_base_data_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用基础数据表';

-- 种子数据：由应用启动类 BaseTreeNodeDataInitializer 按 BaseTreeNodeSeedEnum 幂等插入，
-- 主键 id 由 MyBatis-Plus ASSIGN_ID（雪花）在插入时生成，勿在 SQL 中写死 id。