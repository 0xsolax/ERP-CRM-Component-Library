package com.qmy.project.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "扫码登录参数")
public class ScanLoginDTO {

    @Schema(description = "登录类型: dingtalk-钉钉, feishu-飞书, wecom-企业微信", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录类型不能为空")
    private String type;

    @Schema(description = "第三方平台授权码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "授权码不能为空")
    private String code;
}
