package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报价单会签审核：财务通过 / 总裁通过 / 驳回
 */
@Data
public class SalSedQuotationJointAuditParams {

    @Schema(description = "报价单id")
    @NotNull(message = "报价单id不能为空")
    private Long id;


    @Schema(description = "操作：FINANCE_PASS=财务通过，PRESIDENT_PASS=总裁通过")
    private String action;
}
