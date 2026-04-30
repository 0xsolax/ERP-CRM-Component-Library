package com.qmy.zhongsheng.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通过节点种子标识批量查询基础数据。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "通过节点种子标识批量查询基础数据")
public class BaseDataQueryByNodeKeyDTO {

    @Schema(description = "节点种子标识列表（对应 BaseTreeNodeSeedEnum.nodeKey）", example = "FIELD_MGMT_SIZE_UMBRELLA_FRAME_LENGTH")
    private String nodeKey;
}