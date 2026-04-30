package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品材料 DO（字段对齐 material 表）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_material")
public class ProductMaterialDO extends BaseDO {

    private Long productId;

    private Long materialId;

    private Long categoryId;

    private String categoryName;

    private String name;

    private String size;

    private Integer quantity;

    private BigDecimal price;

    private Integer isBound;
}
