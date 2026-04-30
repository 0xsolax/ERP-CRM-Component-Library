package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品工价信息保存 DTO（字段对齐 process 表，前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品工价信息保存 DTO")
public class ProductProcessPriceSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "工序 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "工序不能为空")
    private Long processId;

    @Schema(description = "工序名称")
    @NotNull(message = "工序名称不能为空")
    private String name;

    @Schema(description = "工序金额")
    @NotNull(message = "工序金额不能为空")
    private BigDecimal price;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
