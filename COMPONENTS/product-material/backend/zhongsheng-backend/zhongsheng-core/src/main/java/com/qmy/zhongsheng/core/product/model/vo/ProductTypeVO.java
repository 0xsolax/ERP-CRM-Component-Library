package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品类型 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品类型 VO")
public class ProductTypeVO {

    @Schema(description = "类型 ID（base_data 主键）")
    private Long typeId;

    @Schema(description = "类型名称")
    private String typeName;
}
