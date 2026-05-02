package com.qiaomoyun.entity.sal.sed;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import java.math.BigDecimal;

/**
 * 报价单实体类
 */
@Data
@TableName("sal_sed_quotation")
public class SalSedQuotation extends BaseEntity{

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报价单编号
     */
    private String quotationCode;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 业务员id
     */
    private Long salesmanId;

//    /**
//     * 订单来源-一键转订单时
//     */
//    private String orderSource;

    /**
     * 采购成本
     */
    private BigDecimal procurementCost;

    /**
     * 采购成本状态 0=待确认 ，1=已确认
     */
    private String procurementCostState;

    /**
     * 物流成本
     */
    private BigDecimal logisticsCost;

    /**
     * 物流成本状态   0=待确认 ，1=已确认
     */
    private String logisticsCostState;

    /**
     * 总成本   0=待确认 ，1=已确认
     */
    private BigDecimal totalCost;

    /**
     * 总成本状态   0=待确认 ，1=已确认
     */
    private String totalCostState;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;


    /**
     * 物流备注
     */
    private String logisticsRemark;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 收货地址id
     */
    private Long receiveAddressId;

    /**
     * 客户报价单收货地址
     */
    private String receiveAddress;

    /**
     * 特殊要求
     */
    private String specialRequirements;

    /**
     * 0=暂存，1=计算成本中，2=计算成本完毕，4=审核通过，5=总裁未审核，财务未审核，6=总裁审核通过，财务未审核，7=总裁未审核，财务审核通过，-1=审核驳回
     */
    private String status;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 一键转换订单转换状态，0=没转换，1=已转换
     */
    private String shiftStatus;

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

}
