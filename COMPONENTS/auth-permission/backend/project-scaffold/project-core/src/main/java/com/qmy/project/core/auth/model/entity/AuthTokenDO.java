package com.qmy.project.core.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_token")
public class AuthTokenDO extends BaseDO {

    @TableField("token_id")
    private String tokenId;

    @TableField("user_id")
    private Long userId;

    @TableField("token")
    private String token;

    @TableField("login_type")
    private String loginType;

    @TableField("platform")
    private String platform;

    @TableField("status")
    private Integer status;

    @TableField("client_ip")
    private String clientIp;

    @TableField("user_agent")
    private String userAgent;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("last_verify_time")
    private LocalDateTime lastVerifyTime;
}
