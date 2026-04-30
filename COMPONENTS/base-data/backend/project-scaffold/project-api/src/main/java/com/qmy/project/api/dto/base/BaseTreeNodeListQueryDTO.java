package com.qmy.project.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author AI Coding
 */
@Data
@Schema(description = "基础树节点查询参数")
public class BaseTreeNodeListQueryDTO {

    @Schema(description = "业务类型，对应 BaseTreeBizTypeEnum")
    private String bizType;
}