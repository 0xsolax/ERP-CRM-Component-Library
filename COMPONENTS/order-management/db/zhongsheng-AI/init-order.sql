-- Source: RAW/PROJECTs/zhongsheng-AI/erp-backend/src/main/resources/init.sql
-- Scope: legacy order CRUD schema used by zhongsheng-AI OrdersController.

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    code VARCHAR(50) NOT NULL COMMENT '订单编号',
    customer_id BIGINT COMMENT '客户ID',
    quote_id BIGINT COMMENT '来源报价单ID',
    order_date DATE COMMENT '下单日期',
    delivery_date DATE COMMENT '要求交期',
    total_amount DECIMAL(12,2) COMMENT '订单总金额',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending待处理 processing生产中 completed已完成 cancelled已取消',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='订单表';

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT COMMENT '产品ID',
    product_name VARCHAR(100) COMMENT '产品名称',
    quantity DECIMAL(10,2) COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    amount DECIMAL(12,2) COMMENT '金额',
    remark VARCHAR(255) COMMENT '备注'
) COMMENT='订单明细表';

-- The legacy finance table has order_id as the order finance reference.
-- Full finance schema belongs to the finance boundary, not this component.
