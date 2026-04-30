package com.qmy.zhongsheng.core.material.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 包材表 {@code packaging}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("packaging")
public class PackagingDO extends BaseDO {

    @TableField("type_id")
    private Long typeId;

    @TableField("type_name")
    private String typeName;

    @TableField("name")
    private String name;

    @TableField("size")
    private String size;

    @TableField("price")
    private BigDecimal price;
}