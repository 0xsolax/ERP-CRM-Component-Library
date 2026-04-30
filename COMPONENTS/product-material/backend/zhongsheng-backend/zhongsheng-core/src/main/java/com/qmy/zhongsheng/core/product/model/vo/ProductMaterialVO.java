package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品材料 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品材料 VO")
public class ProductMaterialVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "材料 ID")
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
    private Integer quantity;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "是否绑定材料")
    private Integer isBound;
}
