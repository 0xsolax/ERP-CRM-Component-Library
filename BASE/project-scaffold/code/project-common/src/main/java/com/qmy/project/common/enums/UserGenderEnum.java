package com.qmy.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Getter
@RequiredArgsConstructor
public enum UserGenderEnum {

    /**
     * 用户性别枚举
     */
    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    private final Integer code;
}
