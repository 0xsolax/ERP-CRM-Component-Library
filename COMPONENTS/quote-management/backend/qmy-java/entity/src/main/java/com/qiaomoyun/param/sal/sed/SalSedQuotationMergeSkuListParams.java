package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合并转订单-获取SKU列表参数
 */
@Data
public class SalSedQuotationMergeSkuListParams {

    @Schema(description = "报价单ID", required = true)
    private Long quotationId;

    @Schema(description = "SKU名称（筛选）")
    private String skuName;

    @Schema(description = "搭配名称（筛选）")
    private String matchName;
}
