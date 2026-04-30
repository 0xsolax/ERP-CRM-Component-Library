package com.qmy.project.common.error;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class GlobalErrorCodeConstants {

    public static final ErrorCode SUCCESS = new ErrorCode(0, "OK");
    public static final ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    public static final ErrorCode UNAUTHORIZED = new ErrorCode(401, "未登录或登录已失效");
    public static final ErrorCode FORBIDDEN = new ErrorCode(403, "无权限访问");
    public static final ErrorCode NOT_FOUND = new ErrorCode(404, "请求资源不存在");
    public static final ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    public static final ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    public static final ErrorCode DATA_NOT_FOUND = new ErrorCode(40404, "数据不存在");

    private GlobalErrorCodeConstants() {
    }
}
