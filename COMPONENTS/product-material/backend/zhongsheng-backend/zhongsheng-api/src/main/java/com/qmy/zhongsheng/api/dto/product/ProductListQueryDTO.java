package com.qmy.zhongsheng.api.dto.product;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品分页查询请求：多维度筛选与关键词模糊搜索。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "产品分页查询请求")
public class ProductListQueryDTO extends BasePageQuery {

    @Schema(description = "产品类型 ID")
    private Long productTypeId;

    @Schema(description = "伞架类型 ID")
    private Long frameTypeId;

    @Schema(description = "伞架尺寸筛选，传 umbrella_frame 表主键 ID，根据该伞架的尺寸查询所有相同尺寸的产品")
    private Long frameLengthId;

    @Schema(description = "伞架功能 ID")
    private Long frameFunctionId;

    @Schema(description = "伞架材料 ID")
    private Long frameMaterialId;

    @Schema(description = "面料种类 ID")
    private Long fabricTypeId;

    @Schema(description = "印刷方式 ID")
    private Long printTypeId;

    @Schema(description = "对齐方式 ID")
    private Long alignmentTypeId;

    @Schema(description = "模糊搜索关键词，支持产品编号、产品类型名称、货品描述（中/英文）")
    private String keywords;
}
