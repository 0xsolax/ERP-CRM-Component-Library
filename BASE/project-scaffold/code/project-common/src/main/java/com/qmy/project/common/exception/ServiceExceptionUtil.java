package com.qmy.project.common.exception;

import com.qmy.project.common.error.ErrorCode;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class ServiceExceptionUtil {

    private ServiceExceptionUtil() {
    }

    public static ServiceException exception(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    public static ServiceException exception(Integer code, String message) {
        return new ServiceException(code, message);
    }
}
