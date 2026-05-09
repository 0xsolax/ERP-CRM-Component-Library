package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 生产分批安排明细 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产分批安排明细 DTO")
public class ProductionBatchItemDTO {

    @NotNull(message = "生产进度行不能为空")
    private Long progressId;

    private String lineKey;

    @NotNull(message = "生产组不能为空")
    private Long productionGroupId;

    @NotNull(message = "安排数量不能为空")
    @DecimalMin(value = "0.01", message = "安排数量必须大于 0")
    private BigDecimal batchQty;

    private LocalDate plannedDeliveryDate;

    private String remark;
}
