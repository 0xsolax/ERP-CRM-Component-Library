package com.qmy.project.core.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "当前登录用户信息")
public class SysUserInfoVO {

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "登录用户名")
    private String userName;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "账号状态: 0正常 1停用")
    private Integer status;

    @Schema(description = "性别: 0未知 1男 2女")
    private Integer gender;

    @Schema(description = "是否超级管理员")
    private Boolean adminFlag;

    @Schema(description = "头像访问地址（由 user.avatar_file_id → system_file.url 解析）")
    private String avatarUrl;

    @Schema(description = "权限信息")
    private Map<String, List<String>> permission = new HashMap<>() {{
        put("curPermissions", Collections.singletonList("*"));
    }};


}
