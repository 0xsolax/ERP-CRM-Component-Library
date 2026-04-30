package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.dto.file.SystemFileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 伞架保存：{@code id} 为空表示新增；非空表示更新。
 * 支持 {@code isDeleted} 字段进行软删除。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "伞架保存（新增或更新）")
public class UmbrellaFrameSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "功能ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "功能不能为空")
    private Long functionId;

    @Schema(description = "类型ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类型不能为空")
    private Long typeId;

    @Schema(description = "伞架长度ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "伞架长度不能为空")
    private Long lengthId;

    @Schema(description = "中棒直径ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "中棒直径不能为空")
    private Long diameterId;

    @Schema(description = "伞骨数量ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "伞骨数量不能为空")
    private Long ribCountId;

    @Schema(description = "材料ID（baseDataId）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "材料不能为空")
    private Long materialId;

    @Schema(description = "特定属性")
    private String specificAttribute;

    @Schema(description = "图片列表")
    private List<SystemFileDTO> images;

    @Schema(description = "单价", requiredMode = Schema.RequiredMode.REQUIRED)
    //@NotNull(message = "单价不能为空")
    @Digits(integer = 20, fraction = 2, message = "金额格式无效：限20位整数及2位小数")
    private BigDecimal price;

    @Schema(description = "单位：支或打", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;

    @Schema(description = "伞架材料绑定列表")
    private List<UmbrellaFrameMaterialSaveDTO> materials;
}