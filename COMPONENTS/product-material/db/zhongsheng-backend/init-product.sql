-- =============================================
-- 产品模块数据库表结构
-- 包含：产品主表、产品伞架关联表、产品材料表、产品面料表、产品印刷表、产品包材表、产品工价表
-- 关联表字段与源表对齐（去业务前缀），便于 BeanUtils 直接转换
-- =============================================

-- 产品表
CREATE TABLE IF NOT EXISTS `product`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_code` VARCHAR(64) NOT NULL COMMENT '产品编号',
  `gross_weight` DECIMAL(10, 2) DEFAULT NULL COMMENT '毛重 (g)',
  `net_weight` DECIMAL(10, 2) DEFAULT NULL COMMENT '净重 (g)',
  `loss_fee` DECIMAL(10, 2) DEFAULT NULL COMMENT '损耗/杂费',
  `description_zh` VARCHAR(2048) DEFAULT NULL COMMENT '货品描述（中文）',
  `description_en` VARCHAR(2048) DEFAULT NULL COMMENT '货品描述（英文）',
  `volume` DECIMAL(10, 6) DEFAULT NULL COMMENT '体积 (m³)',
  `small_cabinet` INT DEFAULT NULL COMMENT '小柜 (20GP) 装箱预估',
  `large_cabinet` INT DEFAULT NULL COMMENT '高柜 (40HC) 装箱预估',
  `total_cost` DECIMAL(20, 2) DEFAULT NULL COMMENT '总成本',
  `selling_price` DECIMAL(20, 2) NOT NULL COMMENT '售价',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code_deleted` (`product_code`, `is_deleted`, `deleted_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品表';

CREATE TABLE IF NOT EXISTS `product_type`
(
  `id` BIGINT NOT NULL COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `type_id` BIGINT NOT NULL COMMENT '类型 ID（base_data 主键）',
  `type_name` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '类型名称（快照）',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_user` BIGINT DEFAULT NULL COMMENT '修改人',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`, `is_deleted`),
  KEY `idx_type_id` (`type_id`, `is_deleted`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品类型关联表';


-- 产品伞架关联表（字段对齐 umbrella_frame 表）
CREATE TABLE IF NOT EXISTS `product_umbrella_frame`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `umbrella_frame_id` BIGINT NOT NULL COMMENT '伞架 ID（关联 umbrella_frame 表）',
  `function_id` BIGINT NOT NULL COMMENT '功能 ID（baseDataId）',
  `function_name` VARCHAR(256) DEFAULT NULL COMMENT '功能名称快照',
  `type_id` BIGINT NOT NULL COMMENT '类型 ID（baseDataId）',
  `type_name` VARCHAR(256) DEFAULT NULL COMMENT '类型名称快照',
  `length_id` BIGINT NOT NULL COMMENT '伞架长度 ID（baseDataId）',
  `length_name` VARCHAR(256) DEFAULT NULL COMMENT '伞架长度名称快照',
  `diameter_id` BIGINT NOT NULL COMMENT '中棒直径 ID（baseDataId）',
  `diameter_name` VARCHAR(256) DEFAULT NULL COMMENT '中棒直径名称快照',
  `rib_count_id` BIGINT NOT NULL COMMENT '伞骨数量 ID（baseDataId）',
  `rib_count_name` VARCHAR(256) DEFAULT NULL COMMENT '伞骨数量名称快照',
  `material_id` BIGINT NOT NULL COMMENT '材料 ID（baseDataId）',
  `material_name` VARCHAR(256) DEFAULT NULL COMMENT '材料名称快照',
  `specific_attribute` VARCHAR(255) DEFAULT NULL COMMENT '特定属性',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `unit` VARCHAR(10) NOT NULL COMMENT '单位：支或打',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品伞架关联表';

-- 产品材料表（字段对齐 material 表）
CREATE TABLE IF NOT EXISTS `product_material`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `material_id` BIGINT NOT NULL COMMENT '材料 ID（关联 material 表）',
  `category_id` BIGINT NOT NULL COMMENT '分类 ID（关联 material_category 表）',
  `category_name` VARCHAR(256) DEFAULT NULL COMMENT '分类名称快照',
  `name` VARCHAR(255) NOT NULL COMMENT '材料名称',
  `size` VARCHAR(64) DEFAULT NULL COMMENT '尺寸',
  `quantity` INT NOT NULL COMMENT '数量',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `is_bound` TINYINT NOT NULL DEFAULT 0 COMMENT '是否绑定材料（0-否，1-是，绑定材料不可删除/编辑）',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品材料表';

-- 产品面料表（字段对齐 fabric 表）
CREATE TABLE IF NOT EXISTS `product_fabric`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `fabric_id` BIGINT NOT NULL COMMENT '面料 ID（关联 fabric 表）',
  `type_id` BIGINT NOT NULL COMMENT '种类 ID（baseDataId）',
  `type_name` VARCHAR(256) NOT NULL COMMENT '种类名称快照',
  `model_id` BIGINT NOT NULL COMMENT '型号 ID（baseDataId）',
  `model_name` VARCHAR(256) DEFAULT NULL COMMENT '型号名称快照',
  `width_id` BIGINT NOT NULL COMMENT '门幅 ID（baseDataId）',
  `width_name` VARCHAR(256) DEFAULT NULL COMMENT '门幅名称快照',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `unit` VARCHAR(10) NOT NULL COMMENT '单位：米或码',
  `usage` DECIMAL(10, 2) NOT NULL COMMENT '用量',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品面料表';

-- 产品印刷表（无源表，去 printing_ 前缀）
CREATE TABLE IF NOT EXISTS `product_printing`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `fabric_type_name` VARCHAR(128) NOT NULL COMMENT '面料类型名称（从面料选择带入）',
  `print_type_id` BIGINT NOT NULL COMMENT '印刷方式 ID（关联基础数据字典）',
  `print_type_name` VARCHAR(128) NOT NULL COMMENT '印刷方式名称',
  `alignment_type_id` BIGINT NOT NULL COMMENT '对齐方式 ID（关联基础数据字典）',
  `alignment_type_name` VARCHAR(128) DEFAULT NULL COMMENT '对齐方式名称',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '印刷价格',
  `plate_fee` DECIMAL(20, 2) DEFAULT NULL COMMENT '版费',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品印刷表';

-- 产品包材表（字段对齐 packaging 表）
CREATE TABLE IF NOT EXISTS `product_packaging`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `packaging_id` BIGINT NOT NULL COMMENT '包材 ID（关联 packaging 表）',
  `type_id` BIGINT NOT NULL COMMENT '包材类型 ID（baseDataId）',
  `type_name` VARCHAR(256) DEFAULT NULL COMMENT '包材类型名称快照',
  `name` VARCHAR(128) NOT NULL COMMENT '包材名称',
  `size` VARCHAR(64) DEFAULT NULL COMMENT '尺寸',
  `box_count` INT DEFAULT NULL COMMENT '装箱数',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '单价',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品包材表';

-- 产品工价表（字段对齐 process 表）
CREATE TABLE IF NOT EXISTS `product_process_price`
(
  `id` BIGINT NOT NULL COMMENT '主键 ID',
  `product_id` BIGINT NOT NULL COMMENT '产品 ID',
  `process_id` BIGINT NOT NULL COMMENT '工序 ID（关联 process 表）',
  `name` VARCHAR(128) NOT NULL COMMENT '工序名称',
  `price` DECIMAL(20, 2) NOT NULL COMMENT '工序金额',
  `create_user` BIGINT NOT NULL DEFAULT -1 COMMENT '创建人 ID',
  `update_user` BIGINT NOT NULL DEFAULT -1 COMMENT '修改人 ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
  `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='产品工价表';
