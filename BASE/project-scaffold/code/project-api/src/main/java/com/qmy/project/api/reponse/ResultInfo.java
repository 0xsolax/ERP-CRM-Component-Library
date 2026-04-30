package com.qmy.project.api.reponse;

import cn.hutool.http.HttpStatus;
import com.qmy.project.common.error.ErrorCode;
import com.qmy.project.common.error.GlobalErrorCodeConstants;
import com.qmy.project.common.exception.ServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "通用返回")
public class ResultInfo<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码", example = "0")
    private Integer code;

    @Schema(description = "响应消息", example = "OK")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "时间戳")
    private long timeStamp = System.currentTimeMillis();

    @Schema(description = "链路标识")
    private String traceId;

    public static <T> ResultInfo<T> success(T data) {
        ResultInfo<T> response = new ResultInfo<>();
        response.setCode(HttpStatus.HTTP_OK);
        response.setMessage("操作成功");
        response.setData(data);
        response.setSuccess(true);
        response.setTraceId(currentTraceId());
        return response;
    }

    public static <T> ResultInfo<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> ResultInfo<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }

    public static <T> ResultInfo<T> error(Integer code, String message) {
        ResultInfo<T> response = new ResultInfo<>();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(false);
        response.setTraceId(currentTraceId());
        return response;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getCode());
    }

    private static String currentTraceId() {
        return MDC.get("traceId");
    }
}
