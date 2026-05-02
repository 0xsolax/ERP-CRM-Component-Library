package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合并转订单-产品行（SKU）信息
 */
@Data
public class SalSedQuotationMergeSkuItemVo {

    @Schema(description = "报价单SKU id")
    private Long quotationSkuId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "搭配名称")
    private String combinationName;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "图片")
    private List<String> pic;

    @Schema(description = "报价")
    private BigDecimal quotationPrice;

    @Schema(description = "数量")
    private Integer quantity;
}
