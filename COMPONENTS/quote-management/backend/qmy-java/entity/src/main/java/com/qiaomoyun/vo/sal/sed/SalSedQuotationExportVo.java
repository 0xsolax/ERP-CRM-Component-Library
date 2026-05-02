package com.qiaomoyun.vo.sal.sed;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalSedQuotationExportVo {
    // 型号名称
    private String item;
    // 产品图片地址
    private String productPhoto;
    // 顶部直径(cm)
    private Double diameterTop;
    // 底部直径(cm)
    private Double diameterBottom;
    // 高度(cm)
    private Double height;
    /** 长度(cm)，与直径/高二选一展示 */
    private Double length;
    /** 宽度(cm) */
    private Double width;
    // 装箱数(pcs)
    private Integer qtyPerCarton;
    // 纸箱尺寸（立方厘米）
    private String cartonSize;
    //cbm（与报价单行「体积(m³)」一致）
    private Double cbm;
    /** 报价单-SKU 表存储的体积(m³)，导出 CBM 以此为准 */
    private BigDecimal volume;
    // 数量
    private Integer moq;
    // 单盆克重
    private BigDecimal weightPerUnit;
    // 王总报价
    private Double bossQuotePrice;
    // 备注
    private String remarks;
    // 宁波FOB单价
    private Double fobNingboPrice;
    //QTY/20FT
    private Integer qtyPer20ft;
    //QTY/40FT
    private Integer qtyPer40ft;

    // 包材尺寸
    private String packingSize;
    // 包材成本
    private BigDecimal packingCost;


    //Sku ID
    private Long skuId;
    //产品id
    private Long productId;
    //报价单-sku表ID
    private Long quotationSkuId;
    //搭配 ID
    private Long matchId;
}
