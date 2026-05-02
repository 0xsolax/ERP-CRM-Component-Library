package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedSkuEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成本明细转换 Vo
 */
@Data
public class SalSedQuotationCostDetailShiftVo {
    @Schema(description = "配件明细")
    private List<SalSedQuotationFittingDetailShiftVo> fittingDetail;
    @Schema(description = "配件总成本")
    private String fittingCost;


    @Schema(description = "效果ID")
    private Long effectId;
    @Schema(description = "效果名称")
    private String effectName;
    @Schema(description = "人工工序量")
    private BigDecimal manualProcessQuantity;
    @Schema(description = "人工成本")
    private BigDecimal manualProcessCost;
    @Schema(description = "油漆工序量")
    private BigDecimal paintingProcessQuantity;
    @Schema(description = "油漆成本")
    private BigDecimal paintingProcessCost;
    @Schema(description = "效果明细")
    private List<ProSedSkuEffect> effectDetail;
    @Schema(description = "效果总成本")
    private String effectCost;

    @Schema(description = "包材明细")
    private List<SalSedQuotationPackagingDetailShiftVo> packagingDetail;
    @Schema(description = "包材总成本")
    private String packagingCost;
}
