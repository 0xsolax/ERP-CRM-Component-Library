package com.qmy.zhongsheng.core.material.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 面料视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "面料视图对象")
public class FabricVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "种类ID（baseDataId）")
    private Long typeId;

    @Schema(description = "种类名称")
    private String typeName;

    @Schema(description = "型号ID（baseDataId）")
    private Long modelId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "门幅ID（baseDataId）")
    private Long widthId;

    @Schema(description = "门幅名称")
    private String widthName;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "单位")
    private String unit;
}