package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalSedHistoryQuotationInfoParams {

    @Schema(description = "skuId")
    @NotNull(message = "skuId不能为空")
    private Long skuId;

    @Schema(description = "产品Id")
    @NotNull(message = "产品Id不能为空")
    private Long productId;

    @Schema(description = "报价单-sku的id")
  //  @NotNull(message = "报价单-sku的id不能为空")
    private Long quotationSkuId;

    @Schema(description = "搭配Id")
    @NotNull(message = "搭配Id不能为空")
    private Long matchId;

    @Schema(description = "客户Id")
    @NotNull(message = "客户Id不能为空")
    private Long customerId;


}
