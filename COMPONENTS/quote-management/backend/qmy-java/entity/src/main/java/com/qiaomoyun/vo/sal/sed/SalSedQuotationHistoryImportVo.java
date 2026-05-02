package com.qiaomoyun.vo.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 报价单历史记录导入
 */
@Data
public class SalSedQuotationHistoryImportVo {
    @Schema(description = "报价单id")
    private Long quotationId;

    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;
}
