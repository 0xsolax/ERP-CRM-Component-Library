package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 伞架表 {@code umbrella_frame}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("umbrella_frame")
public class UmbrellaFrameDO extends BaseDO {

    @TableField("function_id")
    private Long functionId;

    @TableField("function_name")
    private String functionName;

    @TableField("type_id")
    private Long typeId;

    @TableField("type_name")
    private String typeName;

    @TableField("length_id")
    private Long lengthId;

    @TableField("length_name")
    private String lengthName;

    @TableField("diameter_id")
    private Long diameterId;

    @TableField("diameter_name")
    private String diameterName;

    @TableField("rib_count_id")
    private Long ribCountId;

    @TableField("rib_count_name")
    private String ribCountName;

    @TableField("material_id")
    private Long materialId;

    @TableField("material_name")
    private String materialName;

    @TableField("specific_attribute")
    private String specificAttribute;

    @TableField("price")
    private BigDecimal price;

    @TableField("unit")
    private String unit;
}