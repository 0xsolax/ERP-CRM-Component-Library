package com.qmy.project.core.auth.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
public class AuthTokenSession {

    public static final Integer STATUS_ACTIVE = 0;

    public static final Integer STATUS_REVOKED = 1;

    public static final Integer STATUS_EXPIRED = 2;

    private String tokenId;

    private Long userId;

    private String token;

    private String loginType;

    private String platform;

    private Integer status;

    private String clientIp;

    private String userAgent;

    private LocalDateTime expireTime;

    private LocalDateTime lastVerifyTime;
}
