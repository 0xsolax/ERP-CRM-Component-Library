package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 手工生产总单产品行 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "手工生产总单产品行 DTO")
public class ProductionOrderProductDTO {

    @Schema(description = "产品行稳定键")
    private String lineKey;

    @Schema(description = "产品 ID")
    private Long productId;

    @Schema(description = "产品编号")
    private String productCode;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "生产数量")
    @NotNull(message = "生产产品数量不能为空")
    @DecimalMin(value = "0.01", message = "生产产品数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "产品快照 JSON")
    private String sourceSnapshotJson;

    @Schema(description = "产品行备注")
    private String remark;
}
