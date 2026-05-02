package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单详情里面的物流导出
 */

@Data
public class SalYtOrderExportDeliveryParams {
    @Schema(description = "订单ID")
    @NotNull(message = "父订单id不能为空")
    private Long orderId;

    @Schema(description = "发货单id")
    @NotNull(message = "发货单id不能为空")
    private Long deliveryId;

    @Schema(description = "发货单箱子id")
    private Long deliveryBoxId;


}
