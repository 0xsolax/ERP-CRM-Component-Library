package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物流成本明细
 */

@Data
public class SalSedQuotationLogisticsVo {

    @Schema(description = "总运输体积")
    private BigDecimal totalTransportVolume;

    @Schema(description = "总运箱数")
    private Integer totalTransportBox;

    @Schema(description = "客户报价单收货地址")
    private String receiveAddress;


}
