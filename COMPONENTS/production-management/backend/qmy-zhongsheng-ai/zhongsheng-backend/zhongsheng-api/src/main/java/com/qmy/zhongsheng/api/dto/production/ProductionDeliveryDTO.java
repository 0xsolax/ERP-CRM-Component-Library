package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 生产产品行本次交货 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产产品行本次交货 DTO")
public class ProductionDeliveryDTO {

    @NotNull(message = "生产总单不能为空")
    private Long productionOrderId;

    @NotNull(message = "生产进度行不能为空")
    private Long progressId;

    @NotNull(message = "交货数量不能为空")
    @DecimalMin(value = "0.01", message = "交货数量必须大于 0")
    private BigDecimal deliveryQty;

    private String remark;
}
