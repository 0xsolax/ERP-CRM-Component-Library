-- customer-management legacy customer schema
-- Source: RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    code VARCHAR(50) NOT NULL COMMENT '客户编号',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    contact VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0停用',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='客户表';

ALTER TABLE customer ADD COLUMN IF NOT EXISTS type VARCHAR(50) DEFAULT NULL COMMENT '客户类型';
ALTER TABLE customer ADD COLUMN IF NOT EXISTS level VARCHAR(50) DEFAULT NULL COMMENT '客户层级';
ALTER TABLE customer ADD COLUMN IF NOT EXISTS country VARCHAR(100) DEFAULT NULL COMMENT '国家地区';

-- Known mismatch:
-- Customer.java and CustomerServiceImpl reference owner/follower, but this legacy SQL does not create those columns.

