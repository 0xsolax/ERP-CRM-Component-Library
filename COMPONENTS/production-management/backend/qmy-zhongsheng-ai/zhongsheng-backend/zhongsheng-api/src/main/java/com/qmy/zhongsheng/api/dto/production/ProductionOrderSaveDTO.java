package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 手工生产总单保存 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "手工生产总单保存 DTO")
public class ProductionOrderSaveDTO {

    @Schema(description = "生产总单 ID")
    private Long id;

    @Schema(description = "生产总单号，留空自动生成")
    private String code;

    @Schema(description = "客户 ID")
    private Long customerId;

    @Schema(description = "客户名称快照")
    private String customerName;

    @Schema(description = "交期")
    private LocalDate deliveryDate;

    @Schema(description = "备注")
    private String remark;

    @Valid
    @Schema(description = "生产产品行")
    private List<ProductionOrderProductDTO> products;
}
