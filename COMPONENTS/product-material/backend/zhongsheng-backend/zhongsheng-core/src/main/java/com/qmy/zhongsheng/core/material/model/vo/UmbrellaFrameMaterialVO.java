package com.qmy.zhongsheng.core.material.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 伞架材料绑定视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "伞架材料绑定视图对象")
public class UmbrellaFrameMaterialVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "伞架ID")
    private Long umbrellaFrameId;

    @Schema(description = "材料ID（关联material表）")
    private Long materialId;

    @Schema(description = "材料名称")
    private String materialName;

    @Schema(description = "材料分类名称")
    private String materialCategoryName;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "价格")
    private BigDecimal price;
}