CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `user_name` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `password_hash` VARCHAR(255) DEFAULT NULL COMMENT '密码哈希',
  `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `mobile` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0正常 1停用',
  `gender` TINYINT NOT NULL DEFAULT 0 COMMENT '0未知 1男 2女',
  `admin_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '0普通用户 1超级管理员',
  `avatar_file_id` BIGINT DEFAULT NULL COMMENT '头像，对应 system_file.id（url 存于 system_file.url）',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_user_name_is_deleted` (`user_name`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_user_email_is_deleted` (`email`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_user_mobile_is_deleted` (`mobile`, `is_deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `user_bind` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `platform` VARCHAR(32) NOT NULL COMMENT 'feishu/dingtalk/wecom',
  `union_id` VARCHAR(128) DEFAULT NULL COMMENT '第三方 unionId',
  `open_id` VARCHAR(128) DEFAULT NULL COMMENT '第三方 openId',
  `third_user_id` VARCHAR(128) DEFAULT NULL COMMENT '第三方用户 ID',
  `third_nickname`       VARCHAR(64) DEFAULT NULL COMMENT '第三方昵称（与 user.nick_name 区分）',
  `raw_info` TEXT DEFAULT NULL COMMENT '第三方原始信息',
  `last_auth_time` DATETIME DEFAULT NULL COMMENT '最近授权时间',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_bind_user_id` (`user_id`),
  -- 默认：同一 user 可在不同 platform 各有一条绑定（一对多，按平台维度 1:1）
  UNIQUE KEY `uk_user_bind_user_platform_is_deleted` (`user_id`, `platform`, `is_deleted`, `deleted_time`),
  -- 若需「每用户全局仅一条」第三方绑定，请删除上一行唯一键，并启用下一行（二选一）：
  -- UNIQUE KEY `uk_user_bind_user_is_deleted` (`user_id`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_user_bind_platform_union_is_deleted` (`platform`, `union_id`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_user_bind_platform_open_is_deleted` (`platform`, `open_id`, `is_deleted`, `deleted_time`),
  UNIQUE KEY `uk_user_bind_platform_third_is_deleted` (`platform`, `third_user_id`, `is_deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户第三方绑定表';

CREATE TABLE IF NOT EXISTS `user_login_log` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `login_account` VARCHAR(128) DEFAULT NULL COMMENT '登录账号',
  `login_type` VARCHAR(32) NOT NULL COMMENT 'password/feishu_scan/dingtalk_scan/wecom_scan',
  `platform` VARCHAR(32) DEFAULT NULL COMMENT '登录平台',
  `login_status` TINYINT NOT NULL DEFAULT 0 COMMENT '0成功 1失败',
  `token_id` VARCHAR(64) DEFAULT NULL COMMENT 'Token ID',
  `client_ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端 IP',
  `user_agent` VARCHAR(512) DEFAULT NULL COMMENT '用户代理',
  `trace_id` VARCHAR(64) DEFAULT NULL COMMENT '链路追踪 ID',
  `message` VARCHAR(255) DEFAULT NULL COMMENT '附加说明',
  `login_time` DATETIME DEFAULT NULL COMMENT '登录时间',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_login_log_user_id` (`user_id`),
  KEY `idx_user_login_log_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

CREATE TABLE IF NOT EXISTS `auth_token` (
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `token_id` VARCHAR(64) NOT NULL COMMENT 'Token ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `token` VARCHAR(1024) NOT NULL COMMENT '访问令牌',
  `login_type` VARCHAR(32) NOT NULL COMMENT '登录类型',
  `platform` VARCHAR(32) DEFAULT NULL COMMENT '登录平台',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0有效 1吊销 2过期',
  `client_ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端 IP',
  `user_agent` VARCHAR(512) DEFAULT NULL COMMENT '用户代理',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `last_verify_time` DATETIME DEFAULT NULL COMMENT '最后校验时间',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_auth_token_token_id_is_deleted` (`token_id`, `is_deleted`, `deleted_time`),
  KEY `idx_auth_token_user_id` (`user_id`),
  KEY `idx_auth_token_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='JWT 状态表（MySQL 模式）';

-- 账号密码登录请自行插入用户数据，并通过 BCryptPasswordEncoder 生成 password_hash。
