package com.qmy.zhongsheng.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量保存请求体。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础数据批量保存")
public class BaseDataBatchSaveDTO {

    @NotEmpty(message = "保存列表不能为空")
    @Valid
    @Schema(description = "待保存行列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BaseDataBatchItemDTO> items;
}
