package com.qiaomoyun.vo.sto.yt;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 发货单订单详情VO
 */
@Data
public class StoYtDeliveryOrderVo {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 发货方式
     */
    private Integer shippingMethod;

    /**
     * 订单备注
     */
    private String orderRemark;

    /**
     * 交货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date orderDeliveryTime;

    /**
     * 业务员姓名
     */
    private String saleEmployeeName;

    /**
     * 产品状态
     */
    private Boolean productComplete;

    /**
     * 打包状态
     */
    private Boolean packageComplete;
}
