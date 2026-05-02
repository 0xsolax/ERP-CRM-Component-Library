package com.qiaomoyun.param.sal.sed;

import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合并转订单-请选择产品 筛选参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SalSedQuotationMergeListParams extends BasePageQuery {

    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "搭配名称")
    private String matchName;
}
