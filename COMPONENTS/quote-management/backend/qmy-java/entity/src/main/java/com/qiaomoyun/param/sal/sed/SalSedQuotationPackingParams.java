package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购成本确认参数类
 */
@Data
public class SalSedQuotationPackingParams {
    @Schema(description = "包材id")
    private Long packingId;

    @Schema(description = "包材尺寸")
    private String packingSize;

    @Schema(description = "成本单价")
    private BigDecimal costPrice;
}
