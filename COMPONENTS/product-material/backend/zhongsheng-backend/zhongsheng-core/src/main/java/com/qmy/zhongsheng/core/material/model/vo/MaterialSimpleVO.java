package com.qmy.zhongsheng.core.material.model.vo;

import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 材料简单对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "材料简单对象")
public class MaterialSimpleVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "分类ID（关联material_category.id）")
    private Long categoryId;

    @Schema(description = "材料名称")
    private String name;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "价格")
    private BigDecimal price;
}