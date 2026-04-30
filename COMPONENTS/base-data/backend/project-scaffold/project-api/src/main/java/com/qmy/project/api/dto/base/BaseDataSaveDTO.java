package com.qmy.project.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单条保存：{@code id} 为空表示新增（此时 {@code nodeId} 必填）；非空表示更新（仅非 null 字段覆盖库中数据）。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础数据保存（新增或更新）")
public class BaseDataSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "基础树节点 id，关联 base_tree_node；新增时必填（请通过 treeNodeList 查询）", example = "1")
    private Long nodeId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "值1")
    private String value1;

    @Schema(description = "值2")
    private String value2;

    @Schema(description = "值3")
    private String value3;

    @Schema(description = "扩展 JSON 字符串")
    private String extJson;
}
