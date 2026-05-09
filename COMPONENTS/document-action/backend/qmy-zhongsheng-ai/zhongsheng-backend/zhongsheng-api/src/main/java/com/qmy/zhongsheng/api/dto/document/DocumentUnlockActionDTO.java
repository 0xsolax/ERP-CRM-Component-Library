package com.qmy.zhongsheng.api.dto.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公共单据解锁/重新确认动作 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "公共单据解锁/重新确认动作 DTO")
public class DocumentUnlockActionDTO {

    @NotBlank(message = "单据类型不能为空")
    @Schema(description = "单据类型：quote/order/purchase/production")
    private String documentType;

    @NotNull(message = "单据 ID 不能为空")
    @Schema(description = "单据 ID")
    private Long documentId;

    @Schema(description = "动作原因或审批意见")
    private String reason;
}
