package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品工价 DO（字段对齐 process 表）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_process_price")
public class ProductProcessPriceDO extends BaseDO {

    private Long productId;

    private Long processId;

    private String name;

    private BigDecimal price;
}
