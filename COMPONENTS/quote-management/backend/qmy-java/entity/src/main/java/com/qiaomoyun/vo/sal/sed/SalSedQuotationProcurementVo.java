package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购成本明细、报价单详细的包材体积
 */
@Data
public class SalSedQuotationProcurementVo {
//    @Schema(description = "配件id")
//    private Long zhuId;

    @Schema(description = "配件id")
    private Long fittingId;

    @Schema(description = "零件id")
    private Long partId;

    @Schema(description = "sku-包材-id")
    private Long id;

    @Schema(description = "包材id")
    private Long packingId;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "数量")
    private Integer boxMum;

    @Schema(description = "成本单价")
    private BigDecimal costPrice;

    @Schema(description = "包材尺寸")
    private String packingSize;

    @Schema(description = "成本")
    private BigDecimal cost;

    @Schema(description = "维度")
    private String latitude;

    @Schema(description = "数值")
    private String value;

//    @Schema(description = "包材附件信息")
//    private List<ProSedFile> attachmentsList;

}
