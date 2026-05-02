package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalYtOrderCloseParams {
    @Schema(description = "父订单ID")
    @NotNull(message = "父订单ID不能为空")
    private Long orderId;

    @Schema(description = "关闭其他金额")
    @NotNull(message = "关闭其他金额不能为空")
    private BigDecimal otherAmount;

    @Schema(description = "关闭金额")
    @NotNull(message = "关闭金额不能为空")
    private BigDecimal amount;

    @Schema(description = "关闭退回明细")
    @Valid
    @NotEmpty(message = "关闭退回明细不能为空")
    private List<SalYtOrderCloseItemParams> itemList;
}
