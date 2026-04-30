package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品印刷 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品印刷 VO")
public class ProductPrintingVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "面料类型名称")
    private String fabricTypeName;

    @Schema(description = "印刷方式 ID")
    private Long printTypeId;

    @Schema(description = "印刷方式名称")
    private String printTypeName;

    @Schema(description = "对齐方式 ID")
    private Long alignmentTypeId;

    @Schema(description = "对齐方式名称")
    private String alignmentTypeName;

    @Schema(description = "印刷价格")
    private BigDecimal price;

    @Schema(description = "版费")
    private BigDecimal plateFee;

}
