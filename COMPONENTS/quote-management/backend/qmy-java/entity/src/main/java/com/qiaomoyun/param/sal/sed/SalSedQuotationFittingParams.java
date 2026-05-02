package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购成本配件信息
 */
@Data
public class SalSedQuotationFittingParams {
    @Schema(description = "配件id")
    private Long fittingId;

    @Schema(description = "成本单价")
    private BigDecimal costPrice;
}
