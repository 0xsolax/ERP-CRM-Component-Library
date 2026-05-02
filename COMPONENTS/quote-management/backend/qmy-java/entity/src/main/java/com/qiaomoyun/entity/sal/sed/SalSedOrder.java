package com.qiaomoyun.entity.sal.sed;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sal_sed_order")
public class SalSedOrder extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单编号
     */
    private String code;
    /**
     * 订单来源
     */
    private String orderResource;
    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 跟进人id
     */
    private Long userId;
    /**
     * 特殊要求
     */
    private String specialRequirements;
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    /**
     * 业务员id
     */
    private Long salesmanId;
    /**
     * 报价单编号
     */
    private Long quotationId;
    /**
     * 交货日期
     */
    private LocalDateTime deliveryDate;
    /**
     * 收货地址id
     */
    private Long receiveAddressId;
    /**
     * 收货地址
     */
    private String receiveAddress;
    /**
     * 第三方平台编号
     */
    private Long thirdPlatformId;
    /**
     * 订单总金额
     */
    private BigDecimal orderMoney;

    /**
     * 第三方平台单号
     */
    private String thirdPlatformOrderCode;

    /**
     * 管家婆同步状态
     */
    private Integer syncStatus;

    /**
     * 同步失败原因
     */
    private String syncFailReason;

    /**
     * 管家婆单据编码
     */
    private String guanjiapoCode;

    /**
     * 币种
     */
    private String currency;

    /**
     * 是否含税
     */
    private String tax;

    /**
     * 装运港
     */
    private String fob;

    /**
     * 指定地点
     */
    private String exw;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 合同编号
     */
    private String contractNumber;
}
