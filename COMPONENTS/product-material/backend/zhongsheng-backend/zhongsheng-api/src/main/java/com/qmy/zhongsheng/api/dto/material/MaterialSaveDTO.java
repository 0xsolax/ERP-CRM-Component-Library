package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.dto.file.SystemFileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 材料保存：{@code id} 为空表示新增；非空表示更新。
 * 支持 {@code isDeleted} 字段进行软删除。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "材料保存（新增或更新）")
public class MaterialSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "分类ID（关联material_category.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "材料名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "黑色伞帽")
    @NotBlank(message = "材料名称不能为空")
    @Size(max = 50, message = "材料名称长度过长")
    private String name;

    @Schema(description = "尺寸", example = "10cm")
    @Size(max = 50, message = "尺寸字符长度超出预期")
    private String size;

    @Schema(description = "价格", example = "5.50")
    @Digits(integer = 20, fraction = 2, message = "金额格式无效：限20位整数及2位小数")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Schema(description = "图片列表")
    private List<SystemFileDTO> images;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}