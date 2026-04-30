package com.qmy.zhongsheng.core.material.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 伞架视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "伞架视图对象")
public class UmbrellaFrameVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "功能ID（baseDataId）")
    private Long functionId;

    @Schema(description = "功能名称")
    private String functionName;

    @Schema(description = "类型ID（baseDataId）")
    private Long typeId;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "伞架长度ID（baseDataId）")
    private Long lengthId;

    @Schema(description = "伞架长度名称")
    private String lengthName;

    @Schema(description = "中棒直径ID（baseDataId）")
    private Long diameterId;

    @Schema(description = "中棒直径名称")
    private String diameterName;

    @Schema(description = "伞骨数量ID（baseDataId）")
    private Long ribCountId;

    @Schema(description = "伞骨数量名称")
    private String ribCountName;

    @Schema(description = "材料ID（baseDataId）")
    private Long materialId;

    @Schema(description = "材料名称")
    private String materialName;

    @Schema(description = "特定属性")
    private String specificAttribute;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "绑定的材料列表")
    private List<UmbrellaFrameMaterialVO> materials;
}