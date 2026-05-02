package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalSedQuotationExportParams {

    @Schema(description = "报价单id")
    @NotNull(message = "报价单id不能为空")
    private Long quotationId;
}
