package com.qiaomoyun.param.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单个 SKU 转订单接收参数类
 */
@Data
public class SalSedQuotationSkuToOrderParams {
    @Schema(description = "报价单 id")
    @NotNull(message = "报价单 id 不能为空")
    private Long quotationId;

    @Schema(description = "报价单 SKU id")
    @NotNull(message = "报价单 SKU id 不能为空")
    private Long quotationSkuId;

    @Schema(description = "订单来源 1=外贸，2=义乌 ")
    @NotNull(message = "订单来源不能为空")
    private String orderSource;

    @Schema(description = "交货日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "交货日期不能为空")
    private LocalDateTime deliveryDate;

    @Schema(description = "创建人 ID")
    private Long createUser;

    @Schema(description = "合同编号")
    private String contractNumber;

}