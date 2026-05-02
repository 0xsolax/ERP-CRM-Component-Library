package com.qiaomoyun.entity.sal.sed;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情
 */
@Data
@TableName("sal_sed_order_detail")
public class SalSedOrderDetail extends BaseEntity {
    /**
     * 订单详情id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 报价单 sku 包材 id
     */
    private Long quotationSkuPackingId;
    /**
     * 所需包材数量
     */
    private Integer boxNum;
    /**
     * 装箱数
     */
    private Integer packingNum;
    /**
     * 包材 id
     */
    private Long packingId;
    /**
     * SKUid
     */
    private Long skuId;
    /**
     * 报价单 sku id
     */
    private Long quotationSkuId;
    /**
     * 产品id
     */
    private Long productId;
    /**
     * 报价单 id
     */
    private Long quotationId;
    /**
     * 报价
     */
    private BigDecimal quotationPrice;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 体积
     */
    private BigDecimal volume;
    /**
     * 搭配 id
     */
    private Long matchId;

    /**
     * 包材成本
     */
    private BigDecimal packingCost;

    /**
     * 包材尺寸
     */
    private String packingSize;

    /**
     * 备注
     */
    private String remark;
}

