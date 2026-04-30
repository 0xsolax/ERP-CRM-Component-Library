package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 面料分页查询：支持种类、型号、门幅筛选。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "面料分页查询")
public class FabricListQueryDTO extends BasePageQuery {

    @Schema(description = "种类ID（baseDataId）", example = "1")
    private Long typeId;

    @Schema(description = "型号ID（baseDataId）", example = "2")
    private Long modelId;

    @Schema(description = "关键词", example = "关键词")
    private String keywords;
}