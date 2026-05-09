package com.qmy.zhongsheng.core.production.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 生产总单 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("production_order")
public class ProductionOrderDO extends BaseDO {

    @TableField("code")
    private String code;

    @TableField("base_code")
    private String baseCode;

    @TableField("serial_code")
    private String serialCode;

    @TableField("order_type")
    private String orderType;

    @TableField("master_order_key")
    private String masterOrderKey;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_code")
    private String orderCode;

    @TableField(value = "customer_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long customerId;

    @TableField(value = "customer_name", updateStrategy = FieldStrategy.ALWAYS)
    private String customerName;

    @TableField(value = "delivery_date", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate deliveryDate;

    @TableField("status")
    private String status;

    @TableField("lock_state")
    private String lockState;

    @TableField("needs_reconfirm")
    private Boolean needsReconfirm;

    @TableField(value = "reconfirm_scope_json", updateStrategy = FieldStrategy.ALWAYS)
    private String reconfirmScopeJson;

    @TableField(value = "owner_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long ownerId;

    @TableField(value = "owner_name", updateStrategy = FieldStrategy.ALWAYS)
    private String ownerName;

    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;
}
