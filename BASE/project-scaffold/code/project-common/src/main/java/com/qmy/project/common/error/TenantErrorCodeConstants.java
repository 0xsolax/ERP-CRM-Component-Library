package com.qmy.project.common.error;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
public final class TenantErrorCodeConstants {

    public static final ErrorCode TENANT_CONFIG_NOT_FOUND = new ErrorCode(40420, "租户配置不存在");

    public static final ErrorCode TENANT_DOMAIN_MISMATCH = new ErrorCode(40421, "域名与当前系统配置不一致");

    public static final ErrorCode TENANT_ID_MISMATCH = new ErrorCode(40320, "租户 id 与当前实例配置不一致");

    public static final ErrorCode SUPER_ADMIN_ACCOUNT_SYSTEM_KEY_INVALID = new ErrorCode(40021, "指定 thirdBindAdminUserId 时，accountSystemKey 须能识别为钉钉或飞书（如 DingDing、FeiShu，或与平台 code 一致）");

    private TenantErrorCodeConstants() {
    }
}
