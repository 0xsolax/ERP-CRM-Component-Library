package com.qmy.zhongsheng.common.enums;

import com.qmy.zhongsheng.common.utils.ValidityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author AI Coding
 * @description ScanLoginTypeEnum
 * @date 2026/03/20 09:49
 */
@Getter
@RequiredArgsConstructor
public enum ScanLoginTypeEnum {

    /**
     * 扫码登录平台枚举
     */
    FEISHU("feishu", "飞书", LoginTypeEnum.FEISHU_SCAN),
    DINGTALK("dingtalk", "钉钉", LoginTypeEnum.DINGTALK_SCAN),
    WECOM("wecom", "企业微信", LoginTypeEnum.WECOM_SCAN);

    private final String code;

    private final String description;

    private final LoginTypeEnum loginType;

    public static ScanLoginTypeEnum getByCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 中台账号体系 Key（如 DingDing、FeiShu）或与 {@link #code} 一致时，解析为钉钉/飞书扫码类型；其余或未识别返回 {@code null}。
     */
    public static ScanLoginTypeEnum fromAccountSystemKey(String accountSystemKey) {
        if (ValidityUtils.isBlank(accountSystemKey)) {
            return null;
        }
        String k = accountSystemKey.trim();
        ScanLoginTypeEnum byCode = getByCode(k);
        if (byCode == DINGTALK || byCode == FEISHU) {
            return byCode;
        }
        return switch (k.toUpperCase(Locale.ROOT)) {
            case "DINGDING" -> DINGTALK;
            case "FEISHU", "LARK" -> FEISHU;
            default -> null;
        };
    }
}
