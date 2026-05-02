package com.qiaomoyun.param.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 合并转订单-提交参数
 */
@Data
public class SalSedQuotationMergeToOrderParams {

    @Schema(description = "选中的报价单 SKU id 列表（可来自不同报价单）")
    @NotEmpty(message = "请至少选择一个 SKU")
    private List<Long> quotationSkuIds;

    @Schema(description = "订单来源 1=外贸，2=义乌")
    @NotNull(message = "订单来源不能为空")
    private String orderSource;

    @Schema(description = "交货日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotNull(message = "交货日期不能为空")
    private LocalDateTime deliveryDate;

    @Schema(description = "创建人ID")
    private Long createUser;

    @Schema(description = "合同编号")
    private String contractNumber;
}
