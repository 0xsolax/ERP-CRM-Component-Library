package com.qmy.zhongsheng.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author AI Coding
 * @description ErrorCode
 * @date 2026/03/20 09:49
 */
@Getter
@AllArgsConstructor
public class ErrorCode {

    private final Integer code;
    private final String message;
}
