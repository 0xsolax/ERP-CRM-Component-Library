package com.qmy.zhongsheng.core.material.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 包材视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "包材视图对象")
public class PackagingVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "包材类型ID（baseDataId）")
    private Long typeId;

    @Schema(description = "包材类型名称快照")
    private String typeName;

    @Schema(description = "包材名称")
    private String name;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "单价")
    private BigDecimal price;
}