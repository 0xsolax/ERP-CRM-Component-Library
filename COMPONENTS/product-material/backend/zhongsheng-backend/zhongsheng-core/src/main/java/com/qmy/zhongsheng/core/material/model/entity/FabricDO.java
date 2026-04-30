package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 面料表 {@code fabric}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fabric")
public class FabricDO extends BaseDO {

    @TableField("type_id")
    private Long typeId;

    @TableField("type_name")
    private String typeName;

    @TableField("model_id")
    private Long modelId;

    @TableField("model_name")
    private String modelName;

    @TableField("width_id")
    private Long widthId;

    @TableField("width_name")
    private String widthName;

    @TableField("price")
    private BigDecimal price;

    @TableField("unit")
    private String unit;
}