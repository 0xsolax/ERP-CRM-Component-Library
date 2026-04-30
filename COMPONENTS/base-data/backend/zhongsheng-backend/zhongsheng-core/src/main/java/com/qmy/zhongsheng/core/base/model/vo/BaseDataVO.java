package com.qmy.zhongsheng.core.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础数据展示对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础数据")
public class BaseDataVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "基础树节点 id")
    private Long nodeId;

    @Schema(description = "业务类型（来自树节点），FIELD_MGMT / CATEGORY", example = "FIELD_MGMT")
    private String bizType;

    @Schema(description = "树节点名称（来自 base_tree_node.name）")
    private String nodeName;

    @Schema(description = "树节点key（来自 base_tree_node.nodeKey）")
    private String nodeKey;

    @Schema(description = "值1")
    private String value1;

    @Schema(description = "值2")
    private String value2;

    @Schema(description = "值3")
    private String value3;

    @Schema(description = "值4")
    private String value4;
}
