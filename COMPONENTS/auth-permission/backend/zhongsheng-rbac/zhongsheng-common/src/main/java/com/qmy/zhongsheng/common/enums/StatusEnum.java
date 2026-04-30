package com.qmy.zhongsheng.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author AI Coding
 * @description StatusEnum
 * @date 2026/03/20 09:49
 */
@Getter
@RequiredArgsConstructor
public enum StatusEnum {

    /**
     * 状态枚举 0 正常 1 禁用
     */
    NORMAL(0),
    DISABLED(1);

    private final Integer code;
}
