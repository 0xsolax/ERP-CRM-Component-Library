package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 伞架分页查询：含分页参数与关键词；支持功能、类型、尺寸、材料筛选。
 * 下拉不分页场景请使用 {@link UmbrellaFrameSelectQueryDTO}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "伞架分页查询")
public class UmbrellaFrameListQueryDTO extends BasePageQuery {

    @Schema(description = "功能ID（baseDataId）")
    private Long functionId;

    @Schema(description = "类型ID（baseDataId）")
    private Long typeId;

    @Schema(description = "伞架长度ID（baseDataId）")
    private Long lengthId;

    @Schema(description = "中棒直径ID（baseDataId）")
    private Long diameterId;

    @Schema(description = "伞骨数量ID（baseDataId）")
    private Long ribCountId;

    @Schema(description = "材料ID（baseDataId）")
    private Long materialId;

    @Schema(description = "关键词（对功能/类型/长度/直径/伞骨数量/材料名称/特定属性做模糊匹配）")
    private String keywords;
}