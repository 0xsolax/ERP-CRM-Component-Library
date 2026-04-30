package com.qmy.zhongsheng.api.dto.material;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 伞架下拉列表查询：按功能、类型、尺寸、材料（均为 baseDataId）筛选，不分页、无关键词。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "伞架下拉列表查询")
public class UmbrellaFrameSelectQueryDTO {

    @Schema(description = "功能ID（baseDataId），不传则不过滤")
    private Long functionId;

    @Schema(description = "类型ID（baseDataId），不传则不过滤")
    private Long typeId;

    @Schema(description = "伞架长度ID（baseDataId），不传则不过滤")
    private Long lengthId;

    @Schema(description = "中棒直径ID（baseDataId），不传则不过滤")
    private Long diameterId;

    @Schema(description = "伞骨数量ID（baseDataId），不传则不过滤")
    private Long ribCountId;

    @Schema(description = "材料ID（baseDataId），不传则不过滤")
    private Long materialId;
}
