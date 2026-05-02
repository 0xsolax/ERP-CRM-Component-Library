package com.qiaomoyun.param.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增、编辑、再次创建报价单SKU信息的参数类
 */
@Data
public class SalSedQuotationSkuParams {

    @Schema(description = "SKU表的id")
    private Long id;

    @Schema(description = "产品id")
    @NotNull(message = "产品id不能为空")
    private Long productId;

    @Schema(description = "搭配id")
    @NotNull(message = "搭配id不能为空")
    private Long matchId;

    @Schema(description = "SKUid")
    @NotNull(message = "SKUid不能为空")
    private Long skuId;

    @Schema(description = "报价")
    @NotNull(message = "报价不能为空")
    private BigDecimal quotationPrice;

    @Schema(description = "基础报价")
    private BigDecimal quotationBasePrice;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @Schema(description = "体积")
    @NotNull(message = "体积不能为空")
    private BigDecimal volume;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "删除标识，删除传入1，修改传入0")
    private Integer isDeleted;

    @Schema(description = "包材信息")
    private List<SalSedQuotationSkuPackingParams> packing;
}
