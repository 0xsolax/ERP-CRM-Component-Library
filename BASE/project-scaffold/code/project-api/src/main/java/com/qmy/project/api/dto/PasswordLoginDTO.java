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
@Schema(description = "账号密码登录参数")
public class PasswordLoginDTO {

    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录用户名不能为空")
    private String userName;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录密码不能为空")
    private String password;
}
