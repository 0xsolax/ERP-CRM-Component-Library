package com.qmy.zhongsheng.core.production.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产总单分批安排 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("production_order_batch")
public class ProductionOrderBatchDO extends BaseDO {

    @TableField("production_order_id")
    private Long productionOrderId;

    @TableField("progress_id")
    private Long progressId;

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

    @TableField("production_group_id")
    private Long productionGroupId;

    @TableField("production_group_name")
    private String productionGroupName;

    @TableField("batch_qty")
    private BigDecimal batchQty;

    @TableField(value = "planned_delivery_date", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate plannedDeliveryDate;

    @TableField("status")
    private String status;

    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;
}
