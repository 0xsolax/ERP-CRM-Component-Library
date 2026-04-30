CREATE TABLE IF NOT EXISTS `system_menu` (
  `id` BIGINT NOT NULL COMMENT '主键 ID（雪花算法）',
  `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `permission` VARCHAR(100) NOT NULL COMMENT '权限标识',
  `type` TINYINT NOT NULL COMMENT '菜单类型：-1 未知，1-目录，2-菜单，3-按钮',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级菜单 ID，根节点为 0',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '前端路由路径',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标类名',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件路径',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见：0-隐藏，1-显示',
  `keep_alive` TINYINT NOT NULL DEFAULT 1 COMMENT '是否缓存页面：0-不缓存，1-缓存',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_is_deleted_deleted_time` (`permission`, `is_deleted`, `deleted_time`),
  KEY `idx_system_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL COMMENT '主键 ID（雪花算法）',
  `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `desc` VARCHAR(512) DEFAULT NULL COMMENT '角色描述',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name_is_deleted` (`name`, `is_deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS `role_menu` (
  `id` BIGINT NOT NULL COMMENT '主键 ID（雪花算法）',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID，关联 role.id',
  `menu_id` BIGINT NOT NULL COMMENT '菜单 ID，关联 system_menu.id',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu_role_menu_deleted` (`role_id`, `menu_id`, `is_deleted`, `deleted_time`),
  KEY `idx_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与菜单关联表';

CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL COMMENT '主键 ID（雪花算法）',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID，关联 user.id',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID，关联 role.id',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role_user_role_deleted` (`user_id`, `role_id`, `is_deleted`, `deleted_time`),
  KEY `idx_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与角色关联表';
