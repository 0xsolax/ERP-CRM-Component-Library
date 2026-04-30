package com.qmy.zhongsheng.api.dto.material;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 包材分页查询：支持类型、尺寸筛选。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "包材分页查询")
public class PackagingListQueryDTO extends BasePageQuery {

    @Schema(description = "包材类型ID（baseDataId）", example = "1")
    private Long typeId;

    @Schema(description = "尺寸（模糊查询）", example = "10*20")
    private String likeSize;

    @Schema(description = "关键词，支持包材类型名称、包材名称、尺寸、单价模糊搜索", example = "PP袋")
    private String keywords;

    @Schema(description = "是否为默认类型包材")
    private String defaultTypeFlag;
}