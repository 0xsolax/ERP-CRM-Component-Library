package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品 DO。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class ProductDO extends BaseDO {

    /**
     * 产品编号
     */
    private String productCode;

    /**
     * 毛重 (g)
     */
    private BigDecimal grossWeight;

    /**
     * 净重 (g)
     */
    private BigDecimal netWeight;

    /**
     * 损耗/杂费
     */
    private BigDecimal lossFee;

    /**
     * 货品描述（中文）
     */
    private String descriptionZh;

    /**
     * 货品描述（英文）
     */
    private String descriptionEn;

    /**
     * 体积 (m³)
     */
    private BigDecimal volume;

    /**
     * 小柜 (20GP) 装箱预估
     */
    private Integer smallCabinet;

    /**
     * 高柜 (40HC) 装箱预估
     */
    private Integer largeCabinet;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

    /**
     * 售价
     */
    private BigDecimal sellingPrice;
}
