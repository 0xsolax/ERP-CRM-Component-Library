package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 材料分类列表查询：支持按名称模糊查询。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "材料分类列表查询")
public class MaterialCategoryListQueryDTO {

    @Schema(description = "分类名称（模糊查询）", example = "伞")
    private String likeName;
}
