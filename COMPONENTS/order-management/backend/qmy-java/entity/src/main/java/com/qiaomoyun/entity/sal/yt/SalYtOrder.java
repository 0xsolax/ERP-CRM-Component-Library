package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_yt_order")
public class SalYtOrder extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String sourcePlatform;
    private String platformOrderCode;
    private String currency;
    private Long customerId;
    private String customerAddress;
    private String receiver;
    private String receiverPhone;
    private Long customerAddressId;
    private Long followEmployeeId;
    private Integer followRatio;
    private Long saleEmployeeId;
    private Integer saleRatio;
    private Integer status;
    private String auditOpinion;
    /**
     * 发货形式
     */
    private String shippingMethod;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 交货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否入库发货
     */
    private Boolean isInboundDelivery;

    /**
     * 是否已收运费
     */
    private Integer isCollectedShippingCost;

    /**
     * 运费
     */
    private BigDecimal shippingCost;

    /**
     *订单完成时间
     */
    private LocalDateTime orderFinishTime;

    /**
     * 回款状态(订单)
     */
    private Integer receiveStatus;

    /**
     * 回款完成时间（订单回款）
     */
    private LocalDateTime receiveFinishTime;

    /**
     * 运费回款状态
     */
    private Integer shippingReceiveStatus;

    /**
     * 客户运费回款完成时间(发货完毕与客户运费回款完毕才会有该时间)
     */
    private LocalDateTime shippingReceiveFinishTime;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 关闭其他金额
     */
    private BigDecimal endOtherAmount;

    /**
     * 关闭金额
     */
    private BigDecimal endAmount;

    /**
     * 订单提交时间,不是订单创建时间，订单可能暂存，然后再提交
     */
    private LocalDateTime submitOrderTime;

    /**
     * 订单业务状态：-1=暂存 0=待采购 1=待入库 2=待打包 3=待发货 4=已发货 5=已完成 6=待确认 7=已关闭
     */
    @Schema(description = "订单状态")
    @TableField(exist = false)
    private String orderStatus;

}