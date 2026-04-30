package com.qmy.zhongsheng.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author AI Coding
 * @description LoginTypeEnum
 * @date 2026/03/20 09:49
 */
@Getter
@RequiredArgsConstructor
public enum LoginTypeEnum {

    /**
     * 登录方式枚举
     */
    PASSWORD("password"),
    FEISHU_SCAN("feishu_scan"),
    DINGTALK_SCAN("dingtalk_scan"),
    WECOM_SCAN("wecom_scan");

    private final String code;
}
