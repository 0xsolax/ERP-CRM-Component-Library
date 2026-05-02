/*
 * @author java_deng
 * @date 2025/11/20 15:08
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

@Data
public class SalYtOrderQueryParams extends BasePageQuery {
    private String code;
    private String subCode;
    private String customerName;
    private String platformOrderCode;
    private String currency;
    private Long customerId;
    private Long saleEmployeeId;
    private Long followEmployeeId;
    private String orderType;
    private String itemStatus;
    private Integer status;
    /** 订单业务状态筛选（对应 sal_yt_order.order_status） */
    private String orderStatus;
    // 新增：产品编号
    private String productCode;
}
