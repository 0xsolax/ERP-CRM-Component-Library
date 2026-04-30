package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 材料分页查询：支持分类筛选。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "材料分页查询")
public class MaterialListQueryDTO extends BasePageQuery {

    @Schema(description = "分类ID（关联material_category.id）不传查询全部")
    private Long categoryId;

    @Schema(description = "材料名称（模糊匹配）")
    private String likeName;

    @Schema(description = "尺寸（模糊匹配）")
    private String  likeSize;
}