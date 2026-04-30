package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品印刷 DO（去 printing_ 前缀）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_printing")
public class ProductPrintingDO extends BaseDO {

    private Long productId;

    private String fabricTypeName;

    private Long printTypeId;

    private String printTypeName;

    private Long alignmentTypeId;

    private String alignmentTypeName;

    private BigDecimal price;

    private BigDecimal plateFee;
}
