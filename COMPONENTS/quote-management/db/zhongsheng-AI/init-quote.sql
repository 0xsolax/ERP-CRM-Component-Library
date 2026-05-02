-- Source: RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql
-- Scope: legacy quote CRUD schema used by PRD_Detailed_V2.md.

CREATE TABLE IF NOT EXISTS quote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    code VARCHAR(50) NOT NULL COMMENT '报价单号',
    customer_id BIGINT COMMENT '客户ID',
    quote_date DATE COMMENT '报价日期',
    valid_date DATE COMMENT '有效期',
    total_amount DECIMAL(12,2) COMMENT '报价总金额',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft草稿 confirmed已确认 expired已过期',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='报价表';

CREATE TABLE IF NOT EXISTS quote_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    quote_id BIGINT NOT NULL COMMENT '报价单ID',
    product_id BIGINT COMMENT '产品ID',
    product_name VARCHAR(100) COMMENT '产品名称',
    quantity DECIMAL(10,2) COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    amount DECIMAL(12,2) COMMENT '金额',
    remark VARCHAR(255) COMMENT '备注'
) COMMENT='报价明细表';

-- The legacy orders table has quote_id as the quote source reference.
-- Full order schema belongs to the order-management component.
