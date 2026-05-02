package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 确定物流参数类
 */
@Data
public class SalSedQuotationLogisticsParams {

    @Schema(description = "报价单id")
    private Long id;

    @Schema(description = "业务员id")
    private Long salesmanId;

    @Schema(description = "物流总成本")
    private BigDecimal logisticsCost;

    @Schema(description = "物流备注")
    private String logisticsRemark;


}
