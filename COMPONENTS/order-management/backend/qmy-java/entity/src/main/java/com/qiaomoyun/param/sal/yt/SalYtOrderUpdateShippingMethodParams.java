package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalYtOrderUpdateShippingMethodParams {
    @Schema(description = "父订单ID")
    @NotNull(message = "父订单ID不能为空")
    private Long orderId;

    @Schema(description = "发货方式")
    @NotNull(message = "发货方式不能为空")
    private String shippingMethod;


}
