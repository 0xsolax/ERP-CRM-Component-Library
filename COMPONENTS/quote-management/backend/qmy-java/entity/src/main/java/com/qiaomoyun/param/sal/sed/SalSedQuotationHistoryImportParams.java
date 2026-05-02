package com.qiaomoyun.param.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalSedQuotationHistoryImportParams extends BasePageQuery {
    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createTime;
}
