package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 材料分类表 {@code material_category}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material_category")
public class MaterialCategoryDO extends BaseDO {

    @TableField("name")
    private String name;

    @TableField("sort_num")
    private Integer sortNum;

    @TableField("remark")
    private String remark;
}