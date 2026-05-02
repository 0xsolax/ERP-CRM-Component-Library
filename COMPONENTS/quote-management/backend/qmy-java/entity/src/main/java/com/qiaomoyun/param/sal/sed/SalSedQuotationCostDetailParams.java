package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 成本明细接收参数
 */
@Data
public class SalSedQuotationCostDetailParams {
    @Schema(description = "报价单id")
    @NotNull(message = "报价id单不能为空")
    private Long quotationId;

    @Schema(description = "报价单-sku的id")
    @NotNull(message = "报价单-sku的id不能为空")
    private Long quotationSkuId;

    @Schema(description = "配件id")
    @NotNull(message = "配件id不能为空")
    private Long matchId;

    @Schema(description = "SKUid")
    @NotNull(message = "SKUid不能为空")
    private Long skuId;

}
