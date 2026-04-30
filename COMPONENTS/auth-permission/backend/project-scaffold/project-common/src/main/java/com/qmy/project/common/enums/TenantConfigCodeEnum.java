package com.qmy.project.common.enums;

import com.qmy.project.common.constants.TenantConfigCodeConstants;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:44
 */
@Getter
public enum TenantConfigCodeEnum {

    /**
     * 租户配置信息
     */
    TENANT_ID(TenantConfigCodeConstants.TENANT_ID, "租户id", "当前租户id", ""),
    TENANT_NAME(TenantConfigCodeConstants.TENANT_NAME, "租户名称", "当前租户名称", "巧墨云项目"),
    TENANT_SLOGAN(TenantConfigCodeConstants.TENANT_SLOGAN, "租户 slogan", "租户宣传语 / slogan", null),
    DOMAIN_NAME(TenantConfigCodeConstants.DOMAIN_NAME, "业务域名", "当前租户业务域名", ""),
    FEISHU_WEBHOOK_URL(TenantConfigCodeConstants.FEISHU_WEBHOOK_URL, "飞书告警 Webhook", "服务异常飞书告警地址", ""),
    ACCOUNT_SYSTEM_KEY(TenantConfigCodeConstants.ACCOUNT_SYSTEM_KEY, "账号体系 Key", "租户账号体系标识", null),
    TENANT_STATUS(TenantConfigCodeConstants.TENANT_STATUS, "租户状态", "租户状态（中台约定）", null),
    FEISHU_APP_ID(TenantConfigCodeConstants.FEISHU_APP_ID, "飞书 AppId", "账号体系飞书应用 AppId", null),
    FEISHU_APP_SECRET(TenantConfigCodeConstants.FEISHU_APP_SECRET, "飞书 AppSecret", "账号体系飞书应用 AppSecret", null),
    DINGTALK_APP_KEY(TenantConfigCodeConstants.DINGTALK_APP_KEY, "钉钉 AppKey", "账号体系钉钉应用 AppKey", null),
    DINGTALK_APP_SECRET(TenantConfigCodeConstants.DINGTALK_APP_SECRET, "钉钉 AppSecret", "账号体系钉钉应用 AppSecret", null),
    OSS_ENDPOINT(TenantConfigCodeConstants.OSS_ENDPOINT, "OSS Endpoint", "OSS 访问域名", "oss-cn-hangzhou.aliyuncs.com"),
    OSS_REGION(TenantConfigCodeConstants.OSS_REGION, "OSS Region", "OSS 所属地域", "cn-hangzhou"),
    OSS_ACCESS_KEY_ID(TenantConfigCodeConstants.OSS_ACCESS_KEY_ID, "OSS AccessKeyId", "OSS AccessKeyId", ""),
    OSS_ACCESS_KEY_SECRET(TenantConfigCodeConstants.OSS_ACCESS_KEY_SECRET, "OSS AccessKeySecret", "OSS AccessKeySecret", ""),
    OSS_BUCKET_NAME(TenantConfigCodeConstants.OSS_BUCKET_NAME, "OSS Bucket", "OSS Bucket 名称", ""),
    OSS_STS_REGION_ID(TenantConfigCodeConstants.OSS_STS_REGION_ID, "OSS STS RegionId", "OSS STS RegionId", "cn-hangzhou"),
    OSS_STS_ENDPOINT(TenantConfigCodeConstants.OSS_STS_ENDPOINT, "OSS STS Endpoint", "OSS STS Endpoint", "sts.cn-hangzhou.aliyuncs.com"),
    OSS_STS_ROLE_ARN(TenantConfigCodeConstants.OSS_STS_ROLE_ARN, "OSS STS RoleArn", "OSS STS RoleArn", ""),
    OSS_STS_ROLE_SESSION_NAME(TenantConfigCodeConstants.OSS_STS_ROLE_SESSION_NAME, "OSS STS RoleSessionName", "OSS STS RoleSessionName", "qmy-oss-session"),
    OSS_STS_DURATION_SECONDS(TenantConfigCodeConstants.OSS_STS_DURATION_SECONDS, "OSS STS DurationSeconds", "OSS STS 有效时长秒数", "3600"),
    OSS_STS_POLICY(TenantConfigCodeConstants.OSS_STS_POLICY, "OSS STS Policy", "OSS STS 自定义权限策略", null);

    private static final Map<String, TenantConfigCodeEnum> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(TenantConfigCodeEnum::getCode, Function.identity()));

    private final String code;

    private final String name;

    private final String remark;

    private final String defaultValue;

    TenantConfigCodeEnum(String code, String name, String remark, String defaultValue) {
        this.code = code;
        this.name = name;
        this.remark = remark;
        this.defaultValue = defaultValue;
    }

    /**
     * 按 {@code config_code} 解析枚举，未知 code 返回 {@code null}。
     */
    public static TenantConfigCodeEnum fromCode(String code) {
        return code == null ? null : BY_CODE.get(code);
    }
}
