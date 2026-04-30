package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品包材信息保存 DTO（字段对齐 packaging 表，前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品包材信息保存 DTO")
public class ProductPackagingSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "包材 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "包材不能为空")
    private Long packagingId;

    @Schema(description = "包材类型 ID")
    @NotNull(message = "包材类型不能为空")
    private Long typeId;

    @Schema(description = "包材类型名称")
    @NotNull(message = "包材类型名称不能为空")
    private String typeName;

    @Schema(description = "包材名称")
    @NotNull(message = "包材名称不能为空")
    private String name;

    @Schema(description = "尺寸")
    @NotNull(message = "包材尺寸不能为空")
    private String size;

    @Schema(description = "装箱数")
    @NotNull(message = "包材装箱数不能为空")
    private Integer boxCount;

    @Schema(description = "单价")
    @NotNull(message = "包材单价不能为空")
    private BigDecimal price;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
