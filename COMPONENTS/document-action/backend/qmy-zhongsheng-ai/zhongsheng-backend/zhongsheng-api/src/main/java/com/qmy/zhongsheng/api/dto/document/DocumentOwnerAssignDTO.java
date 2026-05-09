package com.qmy.zhongsheng.api.dto.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公共单据负责人改派 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "公共单据负责人改派 DTO")
public class DocumentOwnerAssignDTO {

    @NotBlank(message = "单据类型不能为空")
    @Schema(description = "单据类型：quote/order/purchase/production")
    private String documentType;

    @NotNull(message = "单据 ID 不能为空")
    @Schema(description = "单据 ID")
    private Long documentId;

    @NotNull(message = "负责人 ID 不能为空")
    @Schema(description = "新负责人 ID")
    private Long ownerId;

    @Schema(description = "改派原因")
    private String reason;
}
