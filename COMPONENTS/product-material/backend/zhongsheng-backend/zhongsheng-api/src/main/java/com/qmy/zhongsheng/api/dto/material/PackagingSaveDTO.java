package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 包材保存：{@code id} 为空表示新增；非空表示更新。
 * 支持 {@code isDeleted} 字段进行软删除。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "包材保存（新增或更新）")
public class PackagingSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "包材类型ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long typeId;

    @Schema(description = "包材类型名称快照")
    private String typeName;

    @NotBlank(message = "包材名称不能为空")
    @Schema(description = "包材名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "10*20PP袋")
    private String name;

    @Schema(description = "尺寸", example = "10*20")
    private String size;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.30")
    @NotNull(message = "单价不能为空")
    @Digits(integer = 20, fraction = 2, message = "金额格式无效：限20位整数及2位小数")
    private BigDecimal price;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}