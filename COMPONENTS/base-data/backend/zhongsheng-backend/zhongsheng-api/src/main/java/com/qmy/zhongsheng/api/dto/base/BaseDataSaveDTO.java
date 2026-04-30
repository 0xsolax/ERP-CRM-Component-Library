package com.qmy.zhongsheng.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Schema(description = "值1")
    @Size(max = 200, message = "值内容不能超过 200 个字符")
    private String value1;

    @Schema(description = "值2")
    @Size(max = 200, message = "值内容不能超过 200 个字符")
    private String value2;

    @Schema(description = "值3")
    @Size(max = 200, message = "值内容不能超过 200 个字符")
    private String value3;

    @Schema(description = "值4")
    @Size(max = 200, message = "值内容不能超过 200 个字符")
    private String value4;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}
