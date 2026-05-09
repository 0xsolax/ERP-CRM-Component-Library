package com.qmy.zhongsheng.api.dto.document;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公共单据动作日志查询 DTO。
 *
 * @author AI Coding
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "公共单据动作日志查询 DTO")
public class DocumentActionLogQueryDTO extends BasePageQuery {

    @NotBlank(message = "单据类型不能为空")
    @Schema(description = "单据类型：quote/order/purchase/production")
    private String documentType;

    @NotNull(message = "单据 ID 不能为空")
    @Schema(description = "单据 ID")
    private Long documentId;
}
