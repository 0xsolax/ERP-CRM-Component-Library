package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 关于报价单的SKU的数据库信息接收类
 */

@Data
public class SalSedQuotationSkuInfoVo {
    @Schema(description = "报价（该报价单币种下的金额）")
    private BigDecimal quotationPrice;

    @Schema(description = "报价单币种")
    private String currency;

    @Schema(description = "报价单汇率（美元时用于折算人民币）")
    private BigDecimal exchangeRate;

    @Schema(description = "从数据库查出的总条数")
    private Integer totalCount;

    @Schema(description = "毛利率")
    private BigDecimal grossProfitRate;
}
