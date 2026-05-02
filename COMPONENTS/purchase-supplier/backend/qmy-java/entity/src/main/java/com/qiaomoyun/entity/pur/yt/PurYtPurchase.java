package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 采购订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_yt_purchase")
public class PurYtPurchase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField("code")
    private String code;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Long supplierId;

    /**
     * 供应商姓名
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 1688单号
     */
    @TableField("order_platform_code")
    private String orderPlatformCode;

    /**
     * 运费
     */
    @TableField("shipping_cost")
    private BigDecimal shippingCost;

    /**
     * 折扣
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 交货时间
     */
    @TableField("delivery_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deliveryTime;

    /**
     * 付款方式
     */
    @TableField("pay_method")
    private String payMethod;

    /**
     * 付款形式
     */
    @TableField("pay_way")
    private String payWay;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime completedTime;

    /**
     * 采购单提交时间
     */
    private LocalDateTime submitPurchaseTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    private Boolean isInboundDelivery;

    @TableField(exist = false)
    private BigDecimal totalAmount;

    @TableField(exist = false)
    private Integer waitEnterNumber;

    @TableField(exist = false)
    private BigDecimal totalPaymentAmount;
}