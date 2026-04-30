package com.qmy.project.core.file.model;

import lombok.Data;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
public class OssStsTokenInfo {

    private String expiration;

    private String accessKeyId;

    private String accessKeySecret;

    private String securityToken;

    private String requestId;

    private String endpoint;

    private String bucketName;
}
