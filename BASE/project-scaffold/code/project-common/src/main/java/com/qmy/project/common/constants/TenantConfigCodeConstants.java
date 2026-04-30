package com.qmy.project.common.constants;

import java.util.List;

/**
 * @author AI Coding
 * @description 租户配置 config_code 常量，及按业务场景聚合的 code 列表（供 {@code TenantConfigQueryCondition} / {@code listByCondition} 使用）。
 * @date 2026/03/20 17:23
 */
public final class TenantConfigCodeConstants {

    public static final String TENANT_ID = "tenant.id";
    public static final String TENANT_NAME = "tenant.name";
    /** 租户 slogan / 宣传语 */
    public static final String TENANT_SLOGAN = "tenant.slogan";
    public static final String DOMAIN_NAME = "tenant.domain-name";
    public static final String FEISHU_WEBHOOK_URL = "tenant.feishu-webhook-url";
    public static final String ACCOUNT_SYSTEM_KEY = "tenant.account-system.key";
    /** 租户状态（如启用/停用，由中台约定取值） */
    public static final String TENANT_STATUS = "tenant.status";
    public static final String FEISHU_APP_ID = "tenant.account-system.feishu-app-id";
    public static final String FEISHU_APP_SECRET = "tenant.account-system.feishu-app-secret";
    public static final String DINGTALK_APP_KEY = "tenant.account-system.dingtalk-app-key";
    public static final String DINGTALK_APP_SECRET = "tenant.account-system.dingtalk-app-secret";
    public static final String OSS_ENDPOINT = "tenant.oss.endpoint";
    public static final String OSS_REGION = "tenant.oss.region";
    public static final String OSS_ACCESS_KEY_ID = "tenant.oss.access-key-id";
    public static final String OSS_ACCESS_KEY_SECRET = "tenant.oss.access-key-secret";
    public static final String OSS_BUCKET_NAME = "tenant.oss.bucket-name";
    public static final String OSS_STS_REGION_ID = "tenant.oss.sts-region-id";
    public static final String OSS_STS_ENDPOINT = "tenant.oss.sts-endpoint";
    public static final String OSS_STS_ROLE_ARN = "tenant.oss.sts-role-arn";
    public static final String OSS_STS_ROLE_SESSION_NAME = "tenant.oss.sts-role-session-name";
    public static final String OSS_STS_DURATION_SECONDS = "tenant.oss.sts-duration-seconds";
    public static final String OSS_STS_POLICY = "tenant.oss.sts-policy";

    /**
     * 飞书告警场景需要拉取的 config_code。
     */
    public static List<String> getTenantFeishuAlertCodes() {
        return List.of(FEISHU_WEBHOOK_URL);
    }

    /**
     * 对外租户信息（名称、域名）场景需要拉取的 config_code。
     */
    public static List<String> getTenantPublicProfileCodes() {
        return List.of(
                TENANT_ID,
                TENANT_NAME,
                TENANT_SLOGAN,
                DOMAIN_NAME,
                ACCOUNT_SYSTEM_KEY,
                FEISHU_APP_ID,
                FEISHU_APP_SECRET,
                DINGTALK_APP_KEY,
                DINGTALK_APP_SECRET
        );
    }

    /**
     * OSS STS 临时凭证及桶信息场景需要拉取的 config_code。
     */
    public static List<String> getTenantOssStsCodes() {
        return List.of(
                OSS_ENDPOINT,
                OSS_REGION,
                OSS_ACCESS_KEY_ID,
                OSS_ACCESS_KEY_SECRET,
                OSS_BUCKET_NAME,
                OSS_STS_REGION_ID,
                OSS_STS_ENDPOINT,
                OSS_STS_ROLE_ARN,
                OSS_STS_ROLE_SESSION_NAME,
                OSS_STS_DURATION_SECONDS,
                OSS_STS_POLICY
        );
    }

    private TenantConfigCodeConstants() {
    }
}
