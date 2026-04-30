package com.qmy.project.core.file.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "OSS STS临时凭证")
public class OssStsTokenVO {

    @Schema(description = "临时访问凭证过期时间")
    private String expiration;

    @Schema(description = "临时访问凭证 AccessKeyId")
    private String accessKeyId;

    @Schema(description = "临时访问凭证 AccessKeySecret")
    private String accessKeySecret;

    @Schema(description = "安全令牌")
    private String securityToken;

    @Schema(description = "请求ID")
    private String requestId;

    @Schema(description = "OSS Endpoint")
    private String endpoint;

    @Schema(description = "OSS Bucket名称")
    private String bucketName;
}

