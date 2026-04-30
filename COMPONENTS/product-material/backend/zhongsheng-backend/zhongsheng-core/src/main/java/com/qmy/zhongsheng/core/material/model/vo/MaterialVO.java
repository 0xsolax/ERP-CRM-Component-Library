package com.qmy.zhongsheng.core.material.model.vo;

import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 材料视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "材料视图对象")
public class MaterialVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "分类ID（关联material_category.id）")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "材料名称")
    private String name;

    @Schema(description = "图片列表")
    private List<FileVO> images;

    @Schema(description = "尺寸")
    private String size;

    @Schema(description = "价格")
    private BigDecimal price;
}