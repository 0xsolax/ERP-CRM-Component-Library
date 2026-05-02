package com.qiaomoyun.vo.sal.sed;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalSedHistoryQuotationCustomerVo {
    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "币种（该条报价对应的币种，前端用于展示¥或$）")
    private String currency;

    @Schema(description = "报价金额（该报价单币种下的金额）")
    private BigDecimal price;

    @Schema(description = "业务员名称")
    private String salerName;


    @Schema(description = "平均报价")
    private BigDecimal averagePrice;

    @Schema(description = "报价日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate quotationDate;

}
