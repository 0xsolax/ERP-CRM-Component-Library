package com.qmy.project.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author AI Coding
 * @description 租户配置保存参数（按 config_code + config_name 做新增/更新）
 * @date 2026/03/20 17:03
 */
@Data
@Schema(description = "租户配置保存参数")
public class TenantConfigSaveDTO {

    @Schema(description = "配置 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "config_code不能为空")
    private String configCode;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "config_name不能为空")
    private String configName;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String configRemark;
}
