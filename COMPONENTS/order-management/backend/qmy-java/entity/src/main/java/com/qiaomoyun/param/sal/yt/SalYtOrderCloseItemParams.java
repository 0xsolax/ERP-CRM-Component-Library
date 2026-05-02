package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalYtOrderCloseItemParams {
    @Schema(description = "订单子项ID")
    @NotNull(message = "订单子项ID不能为空")
    private Long orderSubItemId;

    @Schema(description = "关闭退回数量")
    @NotNull(message = "关闭退回数量不能为空")
    private Integer refundQty;
}
