package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品包材 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品包材 VO")
public class ProductPackagingVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "包材 ID")
    private Long packagingId;

    @Schema(description = "包材类型 ID")
    private Long typeId;

    @Schema(description = "包材类型名称")
    private String typeName;

    @Schema(description = "包材名称")
    private String name;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "装箱数")
    private Integer boxCount;

    @Schema(description = "单价")
    private BigDecimal price;
}
