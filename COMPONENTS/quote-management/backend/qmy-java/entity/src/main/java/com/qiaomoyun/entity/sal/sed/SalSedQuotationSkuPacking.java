package com.qiaomoyun.entity.sal.sed;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 报价单-SKU-包材表实体类
 */
@Data
@TableName("sal_sed_quotation_sku_packing")
public class SalSedQuotationSkuPacking extends BaseEntity {

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
     * 报价单-SKU-id
     */
    private Long quotationSkuId;

    /**
     * 包材id
     */
    private Long packingId;

    /**
     * 附件
     */
    private String attachment;

    /**
     * 所需包材数量
     */
    private Integer boxMum;

    /**
     * 装箱数
     */
    private Integer packingNum;

    /**
     * 成本
     */
    private BigDecimal cost;

    /**
     * 包材尺寸
     */
    private String packingSize;

}
