package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 材料表 {@code material}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material")
public class MaterialDO extends BaseDO {

    @TableField("category_id")
    private Long categoryId;

    @TableField("name")
    private String name;

    @TableField("size")
    private String size;

    @TableField("price")
    private BigDecimal price;
}