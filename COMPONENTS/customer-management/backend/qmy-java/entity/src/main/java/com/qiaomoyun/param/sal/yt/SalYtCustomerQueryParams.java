/*
 * @author java_deng
 * @date 2025/11/14 15:19
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

import java.util.Date;

@Data
public class SalYtCustomerQueryParams extends BasePageQuery {
    private String customerName;
    private String code;
    private Long belongEmployeeId;
    private Long followEmployeeId;
    private String type;

    private Long productId;
    private String specificationName;
    private Long customerId;

    //下拉框选择客户只能选择根据或属于的客户
    private Long belongUserId;

    //消费趋势
    private Date startTime;
    private Date endTime;

    //独立仓
    private Long customerStoreId;
    private Integer recordType;
    private String orderSubCode;
    private String purchaseCode;

    private Integer customerStoreStatus;
}
