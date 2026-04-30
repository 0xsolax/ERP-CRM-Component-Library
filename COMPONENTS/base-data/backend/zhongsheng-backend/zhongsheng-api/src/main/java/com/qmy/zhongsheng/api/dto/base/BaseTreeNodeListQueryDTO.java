package com.qmy.zhongsheng.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author AI Coding
 */
@Data
@Schema(description = "基础树节点查询参数")
public class BaseTreeNodeListQueryDTO {

    @Schema(description = "业务类型，对应 BaseTreeBizTypeEnum：FIELD_MGMT/FABRIC/PACKAGING/PRODUCT")
    private String bizType;

    @Schema(description = "树节点key")
    private String nodeKey;
}