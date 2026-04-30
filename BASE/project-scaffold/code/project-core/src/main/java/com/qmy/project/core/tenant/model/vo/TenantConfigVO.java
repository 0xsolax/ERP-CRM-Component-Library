package com.qmy.project.core.tenant.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
@Data
@Schema(description = "租户配置")
public class TenantConfigVO implements Serializable {

    @Schema(description = "配置 code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configCode;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configName;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置说明")
    private String configRemark;
}
