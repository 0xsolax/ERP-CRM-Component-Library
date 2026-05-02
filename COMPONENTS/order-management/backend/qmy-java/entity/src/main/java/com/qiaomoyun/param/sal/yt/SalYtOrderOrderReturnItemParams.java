package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalYtOrderOrderReturnItemParams {
    @Schema(description = "父订单id")
    @NotNull(message = "父订单id不能为空")
    private Long orderId;

    @Schema(description = "产品id")
    @NotNull(message = "产品id不能为空")
    private Long productId;

    @Schema(description = "单价")
    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    @Schema(description = "定制化属性id")
    private Long labelId;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    private Integer number;

    @Schema(description = "退货原因")
    @NotNull(message = "退货原因不能为空")
    private String reason;



    //产品tab用到的参数
    @Schema(description = "订单规格备注")
    private String remark;
    @Schema(description = "规格id，半成品没有规格")
    private Long specificationId;
    @Schema(description = "成本/供应商价格")
    private BigDecimal supplierPrice;
}
