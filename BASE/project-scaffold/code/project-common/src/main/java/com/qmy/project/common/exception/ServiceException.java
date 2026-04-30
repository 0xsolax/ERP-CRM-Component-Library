package com.qmy.project.common.exception;

import com.qmy.project.common.error.ErrorCode;
import lombok.Getter;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Getter
public class ServiceException extends RuntimeException {

    private final Integer code;

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
