/*
 * @author java_deng
 * @date 2025/12/2 15:23
 * @description 采购单产品查询参数类
 */
package com.qiaomoyun.param.pur.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

/**
 * 采购单产品查询参数类
 */
@Data
public class PurYtPurchaseProductQueryParams extends BasePageQuery {

    /**
     * 采购单ID
     */
    private Long purchaseId;

    /**
     * 订单子ID
     */
    private Long orderSubId;

    /**
     * 规格名称
     */
    private String specificationName;

    /**
     * 产品code
     */
    private String productCode;

    /**
     * 状态：0未确认，1已确认
     */
    private Integer status;

    private String orderSubCode;

    private String orderCode;
}