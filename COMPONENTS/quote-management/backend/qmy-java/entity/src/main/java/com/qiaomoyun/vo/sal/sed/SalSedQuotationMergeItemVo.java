package com.qiaomoyun.vo.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 合并转订单-报价单行（含其下 SKU 列表）
 */
@Data
public class SalSedQuotationMergeItemVo {

    @Schema(description = "报价单id")
    private Long id;

    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员")
    private String salesmanName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "该报价单下的 SKU 列表（可勾选）")
    private List<SalSedQuotationMergeSkuItemVo> skuList;
}
