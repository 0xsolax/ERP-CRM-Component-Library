package com.qiaomoyun.entity.sal.sed;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价单-SKU表实体类
 */
@Data
@TableName("sal_sed_quotation_sku")
public class SalSedQuotationSku extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报价单id
     */
    private Long quotationId;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 搭配id
     */
    private Long matchId;

    /**
     * SKUid
     */
    private Long skuId;

    /**
     * 报价
     */
    private BigDecimal quotationPrice;

    /**
     * 基础报价（固定值，取自产品SKU的basicPrice，不允许编辑）
     */
    private BigDecimal quotationBasePrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 备注
     */
    private String remark;

}
