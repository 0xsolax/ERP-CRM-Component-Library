package com.qmy.zhongsheng.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品伞架信息保存 DTO（字段对齐 umbrella_frame 表，前端可直接 toBean 保存）。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品伞架信息保存 DTO")
public class ProductUmbrellaFrameSaveDTO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "伞架 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "伞架不能为空")
    private Long umbrellaFrameId;

    @Schema(description = "功能 ID")
    private Long functionId;

    @Schema(description = "功能名称")
    private String functionName;

    @Schema(description = "类型 ID")
    private Long typeId;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "伞架长度 ID")
    private Long lengthId;

    @Schema(description = "伞架长度名称")
    private String lengthName;

    @Schema(description = "中棒直径 ID")
    private Long diameterId;

    @Schema(description = "中棒直径名称")
    private String diameterName;

    @Schema(description = "伞骨数量 ID")
    private Long ribCountId;

    @Schema(description = "伞骨数量名称")
    private String ribCountName;

    @Schema(description = "材料 ID")
    private Long materialId;

    @Schema(description = "材料名称")
    private String materialName;

    @Schema(description = "特定属性")
    private String specificAttribute;

    @Schema(description = "单价")
    private BigDecimal price;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数量")
    private Integer quantity;


    @Schema(description = "是否删除")
    private Integer isDeleted;
}
