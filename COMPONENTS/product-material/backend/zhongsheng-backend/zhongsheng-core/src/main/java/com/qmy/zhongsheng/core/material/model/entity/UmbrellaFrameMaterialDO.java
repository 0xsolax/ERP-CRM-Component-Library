package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 伞架材料绑定表 {@code umbrella_frame_material}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("umbrella_frame_material")
public class UmbrellaFrameMaterialDO extends BaseDO {

    @TableField("umbrella_frame_id")
    private Long umbrellaFrameId;

    @TableField("material_id")
    private Long materialId;

    @TableField("material_name")
    private String materialName;

    @TableField("material_category_id")
    private Long materialCategoryId;

    @TableField("material_category_name")
    private String materialCategoryName;

    @TableField("quantity")
    private Integer quantity;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "价格")
    private BigDecimal price;

}