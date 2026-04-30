/*
 * @author java_deng
 * @date 2025/11/20 10:10
 * @description 客户规格映射查询参数
 */
package com.qiaomoyun.param.sal.yt;

import com.baomidou.mybatisplus.annotation.TableField;
import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

/**
 * 客户规格映射查询参数
 */
@Data
public class SalYtCustomerSpecificationComparisonQueryParams extends BasePageQuery {
    private Long customerId;
    private String specification;
    private String customerSpecification;
    private Long productId;

    private String itemNumber;
    private String productCode;

}