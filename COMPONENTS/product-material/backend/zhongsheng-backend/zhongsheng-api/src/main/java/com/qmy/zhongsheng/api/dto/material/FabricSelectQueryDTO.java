package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 面料下拉列表查询：按种类、型号（均为 baseDataId）筛选，不分页。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "面料下拉列表查询")
public class FabricSelectQueryDTO {

    @Schema(description = "种类ID（baseDataId），不传则不过滤种类")
    private Long typeId;

    @Schema(description = "型号ID（baseDataId），不传则不过滤型号")
    private Long modelId;
}
