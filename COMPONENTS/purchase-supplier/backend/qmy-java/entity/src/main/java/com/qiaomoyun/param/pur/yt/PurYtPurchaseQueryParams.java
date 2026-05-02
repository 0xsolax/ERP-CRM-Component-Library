/*
 * @author java_deng
 * @date 2025/12/2 15:23
 * @description 采购订单查询参数类
 */
package com.qiaomoyun.param.pur.yt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

import java.util.Date;

/**
 * 采购订单查询参数类
 */
@Data
public class PurYtPurchaseQueryParams extends BasePageQuery {

    private String code;
    private String status;
    private String minStatus;
    private Long supplierId;
    private String supplierName;
    private String orderPlatformCode;
    private Date deliveryStartTime;
    private Date deliveryEndTime;
    private String payMethod;
    private String payWay;
    private Boolean isInboundDelivery;
    private String orderSubCode;
    private String orderCode;

    //退货记录
    private Long purchaseId;
    private String productCode;
    private String specificationName;

    //跟进记录
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    //退货单退货详情
    private Long itemId;
}
