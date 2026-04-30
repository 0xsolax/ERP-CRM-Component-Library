package com.qmy.zhongsheng.api.dto.process;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工序保存：{@code id} 为空表示新增；非空表示更新。
 * 支持 {@code isDeleted} 字段进行软删除。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "工序保存（新增或更新）")
public class ProcessSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "工序名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "裁剪")
    private String name;

    @Schema(description = "软删除标记：0 正常，1 已删除；传入 1 时执行软删除")
    private Integer isDeleted;
}