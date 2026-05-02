package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报价单SKU信息
 */
@Data
public class SalSedQuotationSkuVo {
    @Schema(description = "报价单-sku的id")
    private Long quotationSkuId;

    @Schema(description = "报价单id")
    private Long quotationId;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "型号名称")
    private String modelName;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "搭配id")
    private Long combinationId;

    @Schema(description = "搭配名称")
    private String combinationName;

    @Schema(description = "SKU的id")
    private Long skuId;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "单个成本")
    private BigDecimal cost;

    @Schema(description = "总成本")
    private BigDecimal totalCost;

    @Schema(description = "图片")
    private List<String> pic;

    @Schema(description = "报价")
    private BigDecimal price;

    @Schema(description = "基础报价（固定值，取自产品SKU的basicPrice）")
    private BigDecimal basicPrice;

    @Schema(description = "币种（取自产品SKU的currency）")
    private String currency;

    @Schema(description = "数量")
    private Integer number;

    @Schema(description = "体积")
    private BigDecimal volume;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "包材id")
    private Long packageId;

    @Schema(description = "包材类型")
    private String packageType;

    @Schema(description = "包材名称")
    private String packageName;

    @Schema(description = "包材尺寸")
    private String packageSize;

    @Schema(description = "装箱数")
    private Integer packingNumber;

    @Schema(description = "包材尺寸（用户输入）")
    private String packingSize;

    @Schema(description = "包材成本（用户输入）")
    private BigDecimal packingCost;

    @Schema(description = "报价单-sku-包材表的id")
    private Long quotationSkuPackingId;

    @Schema(description = "附件信息")
    private List<ProSedFile> attachmentList;

    @Schema(description = "转订单状态：0=未转换，1=已转换")
    private String shiftStatus;

}
