package com.qmy.project.core.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "登录返回结果")
public class UserLoginVO {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "访问令牌")
    private String token;
}
