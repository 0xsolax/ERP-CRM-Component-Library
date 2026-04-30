package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 伞架材料绑定保存。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "伞架材料绑定保存")
public class UmbrellaFrameMaterialSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "材料ID（关联material表）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long materialId;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}