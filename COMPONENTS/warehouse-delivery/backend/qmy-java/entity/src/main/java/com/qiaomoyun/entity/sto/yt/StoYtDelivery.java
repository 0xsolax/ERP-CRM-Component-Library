package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 发货单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_delivery")
public class StoYtDelivery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发货单编号
     */
    @TableField("code")
    private String code;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户地址id
     */
    @TableField("address_id")
    private Long addressId;

    /**
     * 客户地址
     */
    @TableField("address")
    private String address;
    @TableField("consignee")
    private String consignee;
    @TableField("phone")
    private String phone;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date packageTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;
    private String packageCode;
    private Integer receiveStatus;
    /**
     * 运费回款完成时间
     */
    private LocalDateTime receiveFinishTime;

    private Long transportCompanyId;
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Long transportOrderFileId;
    /**
     * 发货金额
     */
    @TableField("delivery_amount")
    private BigDecimal deliveryAmount;

    @TableField(exist = false)
    private String transportCompanyName;
    @TableField(exist = false)
    private String transportOrderFileUrl;
    @TableField(exist = false)
    private String customerName;
    @TableField(exist = false)
    private List<StoYtDeliveryBox> boxList;

    @TableField(exist = false)
    private BigDecimal deliveryProportion; //某子订单产品数量所占发货比例
    @TableField(exist = false)
    private BigDecimal deliveryProportionAmount;
}
