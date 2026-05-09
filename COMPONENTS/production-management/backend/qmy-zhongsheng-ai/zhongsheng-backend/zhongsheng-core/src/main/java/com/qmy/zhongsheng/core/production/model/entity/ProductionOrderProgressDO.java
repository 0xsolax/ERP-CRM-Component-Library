package com.qmy.zhongsheng.core.production.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 生产总单产品行进度 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("production_order_progress")
public class ProductionOrderProgressDO extends BaseDO {

    @TableField("production_order_id")
    private Long productionOrderId;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_code")
    private String orderCode;

    @TableField("line_key")
    private String lineKey;

    @TableField(value = "product_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long productId;

    @TableField(value = "product_code", updateStrategy = FieldStrategy.ALWAYS)
    private String productCode;

    @TableField(value = "product_name", updateStrategy = FieldStrategy.ALWAYS)
    private String productName;

    @TableField("order_qty")
    private BigDecimal orderQty;

    @TableField("planned_qty")
    private BigDecimal plannedQty;

    @TableField("purchased_qty")
    private BigDecimal purchasedQty;

    @TableField("inbound_qty")
    private BigDecimal inboundQty;

    @TableField("produced_qty")
    private BigDecimal producedQty;

    @TableField("delivered_qty")
    private BigDecimal deliveredQty;

    @TableField(value = "source_snapshot_json", updateStrategy = FieldStrategy.ALWAYS)
    private String sourceSnapshotJson;

    @TableField("progress_status")
    private String progressStatus;

    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;
}
