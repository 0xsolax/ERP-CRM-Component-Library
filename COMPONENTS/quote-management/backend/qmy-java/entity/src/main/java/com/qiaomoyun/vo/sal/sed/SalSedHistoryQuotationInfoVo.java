package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedFile;
import com.qiaomoyun.entity.pro.sed.ProSedSkuEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalSedHistoryQuotationInfoVo {
    @Schema(description = "sku图片")
    private List<ProSedFile> pic;

    @Schema(description = "产品型号名称")
    private String productCode;

    //工艺效果和属性
    @Schema(description = "效果明细")
    private List<ProSedSkuEffect> effectDetail;

    @Schema(description = "币种（本次报价对应的币种，前端用于展示¥或$）")
    private String currency;

    @Schema(description = "本次报价（报价单币种下的金额）")
    private BigDecimal thisPrice;

    @Schema(description = "平均报价（始终为人民币）")
    private BigDecimal averagePrice;

    @Schema(description = "报价中位数（始终为人民币）")
    private BigDecimal medianPrice;

    @Schema(description = "毛利率")
    private BigDecimal grossProfitRate;

    @Schema(description = "平均毛利率")
    private BigDecimal averageGrossProfitRate;

    @Schema(description = "毛利率中位数")
    private BigDecimal medianGrossProfitRate;

    //所有客户报价趋势
    @Schema(description = "所有客户报价趋势")
    private List<SalSedHistoryQuotationCustomerVo> allQuotationTrend;


    //本客户报价趋势
    @Schema(description = "本客户报价趋势")
    private List<SalSedHistoryQuotationCustomerVo> thisQuotationTrend;


    //所有客户
    @Schema(description = "所有客户")
    private List<SalSedHistoryQuotationCustomerVo> allCustomer;

    //本客户
    @Schema(description = "本客户")
    private List<SalSedHistoryQuotationCustomerVo> thisCustomer;
}
