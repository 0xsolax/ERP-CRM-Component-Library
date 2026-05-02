package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报价单审核参数类
 */

@Data
public class SalSedQuotationAuditParams {
    @Schema(description = "审核结果：仅支持 -1=审核驳回（需财务或总裁角色）。一键审核通过请使用会签接口 /jointAudit")
    @NotNull(message = "审核结果不能为空")
    private String auditResult;


    @Schema(description = "报价单id")
    @NotNull(message = "报价单id不能为空")
    private Long id;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "业务员id")
    @NotNull(message = "业务员id不能为空")
    private Long salesmanId;
}
