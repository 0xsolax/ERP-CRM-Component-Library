package com.qiaomoyun.param.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedFile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalSedQuotationSkuPackingParams {

    @Schema(description = "报价单-sku-包材表的id")
    private Long id;

    @Schema(description = "包材id")
    @NotNull(message = "包材id不能为空")
    private Long packingId;

    @Schema(description = "附件")
    private List<ProSedFile> attachmentList;

    @Schema(description = "装箱数")
    private Integer packingNum;

    @Schema(description = "成本")
    @NotNull(message = "包材成本不能为空")
    private BigDecimal cost;

    @Schema(description = "包材尺寸")
    @NotNull(message = "包材尺寸不能为空")
    private String packingSize;

    @Schema(description = "删除标识，删除时传入1，修改时传入0")
    private Integer isDeleted;


}
