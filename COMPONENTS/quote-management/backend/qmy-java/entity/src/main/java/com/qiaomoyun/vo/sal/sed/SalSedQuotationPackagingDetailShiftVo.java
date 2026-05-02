package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 成本明细 包材明细转换vo
 */
@Data
public class SalSedQuotationPackagingDetailShiftVo {
    @Schema(description = "包材ID")
    private Long packagingId;

    @Schema(description = "包材名称")
    private String packagingName;

    @Schema(description = "包材尺寸")
    private String packagingSize;

    @Schema(description = "装箱数")
    private Integer packagingNum;

    @Schema(description = "成本")
    private String cost;
}
