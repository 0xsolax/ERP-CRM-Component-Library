package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品面料 DO（字段对齐 fabric 表）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_fabric")
public class ProductFabricDO extends BaseDO {

    private Long productId;

    private Long fabricId;

    private Long typeId;

    private String typeName;

    private Long modelId;

    private String modelName;

    private Long widthId;

    private String widthName;

    private BigDecimal price;

    private String unit;

    @TableField("`usage`")
    private BigDecimal usage;
}
