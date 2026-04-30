package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品印刷信息保存 DTO（前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品印刷信息保存 DTO")
public class ProductPrintingSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "面料类型名称")
    @NotBlank(message = "面料类型不能为空")
    private String fabricTypeName;

    @Schema(description = "印刷方式 ID")
    @NotNull(message = "印刷方式 ID不能为空")
    private Long printTypeId;

    @Schema(description = "印刷方式名称")
    @NotBlank(message = "印刷方式不能为空")
    private String printTypeName;

    @Schema(description = "对齐方式 ID")
    @NotNull(message = "对齐方式 ID不能为空")
    private Long alignmentTypeId;

    @Schema(description = "对齐方式名称")
    @NotBlank(message = "对齐方式不能为空")
    private String alignmentTypeName;

    @Schema(description = "印刷价格")
    @NotNull(message = "印刷价格不能为空")
    private BigDecimal price;

    @Schema(description = "版费")
    private BigDecimal plateFee;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
