package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 面料保存：{@code id} 为空表示新增；非空表示更新。
 * 支持 {@code isDeleted} 字段进行软删除。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "面料保存（新增或更新）")
public class FabricSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "种类ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "种类ID不能为空")
    private Long typeId;

    @Schema(description = "型号ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "型号ID不能为空")
    private Long modelId;

    @Schema(description = "门幅ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "门幅ID不能为空")
    private Long widthId;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "2.40")
    @Digits(integer = 20, fraction = 2, message = "金额格式无效：限20位整数及2位小数")
    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    @Schema(description = "单位：米或码", requiredMode = Schema.RequiredMode.REQUIRED, example = "米")
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}