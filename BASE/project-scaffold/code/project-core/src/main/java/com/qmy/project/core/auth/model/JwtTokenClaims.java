package com.qmy.project.core.auth.model;

import lombok.Data;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
public class JwtTokenClaims {

    private Long userId;

    private String tokenId;

    private String loginType;

    private String platform;

    private Long issuedAt;

    private Long expiresAt;
}
