package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单子表
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_yt_order_sub")
public class SalYtOrderSub extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String subCode;
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单类型
     */
    private String orderType;


    /**
     * 订单来源（废弃）
     */
    private String sourcePlatform;
    /**
     * 订单来源平台单号（废弃）
     */
    private String platformOrderCode;

    /**
     * 发货形式（废弃）
     */
    private String shippingMethod;

    /**
     * 下单时间（废弃）
     */
    private LocalDateTime orderTime;

    /**
     * 交货时间（废弃）
     */
    private LocalDateTime deliveryTime;

    /**
     * 优惠金额（废弃）
     */
    private BigDecimal discountAmount;

    /**
     * 备注（废弃）
     */
    private String remark;

    /**
     * 是否入库发货（废弃）
     */
    private Boolean isInboundDelivery;

    /**
     * 是否已收运费（废弃）
     */
    private Integer isCollectedShippingCost;

    /**
     * 运费(废弃)
     */
    private BigDecimal shippingCost;
    private Integer receiveStatus;

    /**
     * 回款运费
     */
    private BigDecimal receiveShipping;

    /**
     * 回款运费金额本位币，数据为人民币
     */
    private BigDecimal receiveShippingBasePrice;

    /**
     * 付款运费
     */
    private BigDecimal paymentShipping;

    @TableField(exist = false)
    private List<SalYtOrderSubItem> itemList;

    @TableField(exist = false)
    private BigDecimal amount;
    @TableField(exist = false)
    private String subStatus;
    @TableField(exist = false)
    private BigDecimal costAmount; //产品成本
    @TableField(exist = false)
    private BigDecimal profitAmount; //产品利润
    @TableField(exist = false)
    private Map<String,Integer> statusCountMap;


}