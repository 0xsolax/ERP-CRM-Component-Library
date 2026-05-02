package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalSedQuotationFittingDetailVo {

    @Schema(description = "配件ID")
    private Long fittingId;

    @Schema(description = "配件名称")
    private String fittingName;

    @Schema(description = "配件数量")
    private Integer fittingNum;

    @Schema(description = "图片")
    private List<String> pic;

    @Schema(description = "克重")
    private BigDecimal weight;

    @Schema(description = "成本单价")
    //private String unitCost;
    private BigDecimal unitCost;
}
