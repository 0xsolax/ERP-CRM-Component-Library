package com.qmy.project.handler;

import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.common.error.GlobalErrorCodeConstants;
import com.qmy.project.common.exception.ServiceException;
import com.qmy.project.alert.FeishuAlertService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Objects;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final FeishuAlertService feishuAlertService;

    @ExceptionHandler(ServiceException.class)
    public ResultInfo<Void> handleServiceException(ServiceException exception) {
        log.warn("[handleServiceException] code={}, message={}", exception.getCode(), exception.getMessage());
        return ResultInfo.error(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultInfo<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : GlobalErrorCodeConstants.BAD_REQUEST.getMessage();
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "请求参数不正确:" + message);
    }

    @ExceptionHandler(BindException.class)
    public ResultInfo<Void> handleBindException(BindException exception) {
        FieldError fieldError = exception.getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : GlobalErrorCodeConstants.BAD_REQUEST.getMessage();
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "请求参数不正确:" + message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResultInfo<Void> handleConstraintViolationException(ConstraintViolationException exception) {
        ConstraintViolation<?> violation = exception.getConstraintViolations().stream().findFirst().orElse(null);
        String message = violation != null ? violation.getMessage() : GlobalErrorCodeConstants.BAD_REQUEST.getMessage();
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "请求参数不正确:" + message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultInfo<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                "请求参数缺失:" + exception.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultInfo<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                "请求参数类型错误:" + exception.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultInfo<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultInfo<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return ResultInfo.error(GlobalErrorCodeConstants.METHOD_NOT_ALLOWED.getCode(),
                "请求方法不正确:" + exception.getMethod());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultInfo<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        return ResultInfo.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(),
                "请求类型不正确:" + Objects.toString(exception.getContentType(), "unknown"));
    }

    /**
     * 未匹配到 Controller、被当作静态资源处理且文件不存在时（如旧接口路径、拼写错误），返回 404，避免落入通用异常变成 500。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultInfo<Void> handleNoResourceFoundException(NoResourceFoundException exception) {
        log.debug("[handleNoResourceFoundException] resourcePath={}", exception.getResourcePath());
        return ResultInfo.error(GlobalErrorCodeConstants.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResultInfo<Void> handleException(Exception exception) {
        log.error("[handleException]", exception);
        feishuAlertService.sendServerErrorAlert(exception);
        return ResultInfo.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
    }
}
