package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品包材 DO（字段对齐 packaging 表）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_packaging")
public class ProductPackagingDO extends BaseDO {

    private Long productId;

    private Long packagingId;

    private Long typeId;

    private String typeName;

    private String name;

    private String size;

    private Integer boxCount;

    private BigDecimal price;
}
