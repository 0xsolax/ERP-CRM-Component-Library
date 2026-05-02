package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购成本零件信息
 */
@Data
public class SalSedQuotationPartParams {

    @Schema(description = "零件id")
    private Long partId;

    @Schema(description = "成本单价")
    private BigDecimal costPrice;
}
