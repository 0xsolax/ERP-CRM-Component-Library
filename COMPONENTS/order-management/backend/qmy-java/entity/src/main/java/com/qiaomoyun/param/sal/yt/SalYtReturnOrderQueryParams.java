package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 退货订单查询参数
 */
@Data
public class SalYtReturnOrderQueryParams {

    @Schema(description = "父订单ID")
    private Long orderId;

    @Schema(description = "子订单ID")
    private Long orderSubId;

    @Schema(description = "采购单ID")
    private Long purchaseId;

    @Schema(description = "子订单商品项ID")
    private Long orderSubItemId;

    @Schema(description = "产品ID")
    private Long productId;

    private String productCode;

    @Schema(description = "规格ID")
    private Long specificationId;

    @Schema(description = "规格名称")
    private String specificationName;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}