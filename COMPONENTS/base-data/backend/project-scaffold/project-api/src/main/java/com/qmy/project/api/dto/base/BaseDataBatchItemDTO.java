package com.qmy.project.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量保存中的单行：{@code id} 为空表示新增，非空表示按 id 更新。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础数据批量保存项")
public class BaseDataBatchItemDTO {

    @Schema(description = "主键；为空则新增")
    private Long id;

    @Schema(description = "基础树节点 id（请通过 treeNodeList 查询实际值）", example = "1")
    private Long nodeId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "值1")
    private String value1;

    @Schema(description = "值2")
    private String value2;

    @Schema(description = "值3")
    private String value3;

    @Schema(description = "扩展 JSON")
    private String extJson;
}
