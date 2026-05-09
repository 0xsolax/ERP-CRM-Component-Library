package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 生产分批安排保存 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产分批安排保存 DTO")
public class ProductionBatchSaveDTO {

    @NotNull(message = "生产总单不能为空")
    private Long productionOrderId;

    @Valid
    private List<ProductionBatchItemDTO> batches;
}
