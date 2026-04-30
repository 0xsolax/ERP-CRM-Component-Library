package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品材料信息保存 DTO（字段对齐 material 表，前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品材料信息保存 DTO")
public class ProductMaterialSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "材料 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "材料不能为空")
    private Long materialId;

    @Schema(description = "分类 ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "材料名称")
    private String name;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "数量")
    @NotNull(message = "材料数量不能为空")
    private Integer quantity;

    @Schema(description = "单价")
    @NotNull(message = "材料单价不能为空")
    private BigDecimal price;

    @Schema(description = "是否绑定材料：0-否，1-是")
    private Integer isBound;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
