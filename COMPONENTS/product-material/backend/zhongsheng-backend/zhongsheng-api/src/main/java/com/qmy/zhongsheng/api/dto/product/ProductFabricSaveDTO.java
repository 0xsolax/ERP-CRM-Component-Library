package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品面料信息保存 DTO（字段对齐 fabric 表，前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品面料信息保存 DTO")
public class ProductFabricSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "面料 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "面料不能为空")
    private Long fabricId;

    @Schema(description = "种类 ID")
    @NotNull(message = "面料种类 ID 不能为空")
    private Long typeId;

    @Schema(description = "种类名称")
    @NotBlank(message = "面料种类名称不能为空")
    private String typeName;

    @Schema(description = "型号 ID")
    @NotNull(message = "面料型号 ID 不能为空")
    private Long modelId;

    @Schema(description = "型号名称")
    @NotBlank(message = "面料型号名称不能为空")
    private String modelName;

    @Schema(description = "门幅 ID")
    @NotNull(message = "面料门幅 ID 不能为空")
    private Long widthId;

    @Schema(description = "门幅名称")
    @NotBlank(message = "面料门幅不能为空")
    private String widthName;

    @Schema(description = "单价")
    @NotNull(message = "面料单价不能为空")
    private BigDecimal price;

    @Schema(description = "单位")
    @NotBlank(message = "面料单位不能为空")
    private String unit;

    @Schema(description = "用量")
    @NotNull(message = "面料用量不能为空")
    private BigDecimal usage;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
