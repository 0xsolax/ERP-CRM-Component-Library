package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品面料 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品面料 VO")
public class ProductFabricVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "面料 ID")
    private Long fabricId;

    @Schema(description = "种类 ID")
    private Long typeId;

    @Schema(description = "种类名称")
    private String typeName;

    @Schema(description = "型号 ID")
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "门幅 ID")
    private Long widthId;

    @Schema(description = "门幅名称")
    private String widthName;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "用量")
    private BigDecimal usage;
}
