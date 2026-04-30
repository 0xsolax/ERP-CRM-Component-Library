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
@TableName("user_login_log")
public class UserLoginLogDO extends BaseDO {

    @TableField("user_id")
    private Long userId;

    @TableField("login_account")
    private String loginAccount;

    @TableField("login_type")
    private String loginType;

    @TableField("platform")
    private String platform;

    @TableField("login_status")
    private Integer loginStatus;

    @TableField("token_id")
    private String tokenId;

    @TableField("client_ip")
    private String clientIp;

    @TableField("user_agent")
    private String userAgent;

    @TableField("trace_id")
    private String traceId;

    @TableField("message")
    private String message;

    @TableField("login_time")
    private LocalDateTime loginTime;
}
