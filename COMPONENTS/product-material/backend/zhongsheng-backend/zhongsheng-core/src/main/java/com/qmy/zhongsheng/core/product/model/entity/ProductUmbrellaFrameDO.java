package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品伞架关联 DO（字段对齐 umbrella_frame 表）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_umbrella_frame")
public class ProductUmbrellaFrameDO extends BaseDO {

    private Long productId;

    private Long umbrellaFrameId;

    private Long functionId;

    private String functionName;

    private Long typeId;

    private String typeName;

    private Long lengthId;

    private String lengthName;

    private Long diameterId;

    private String diameterName;

    private Long ribCountId;

    private String ribCountName;

    private Long materialId;

    private String materialName;

    private String specificAttribute;

    private BigDecimal price;

    private String unit;

    private Integer quantity;
}
