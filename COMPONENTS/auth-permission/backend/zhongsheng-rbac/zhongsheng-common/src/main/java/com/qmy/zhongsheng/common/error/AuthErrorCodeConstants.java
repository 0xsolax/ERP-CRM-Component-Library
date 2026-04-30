package com.qmy.zhongsheng.common.error;

/**
 * @author AI Coding
 * @description AuthErrorCodeConstants
 * @date 2026/03/20 09:49
 */
public final class AuthErrorCodeConstants {

    public static final ErrorCode ACCOUNT_OR_PASSWORD_INVALID = new ErrorCode(40101, "账号或密码错误");
    public static final ErrorCode ACCOUNT_DISABLED = new ErrorCode(40102, "账号已停用");
    public static final ErrorCode TOKEN_INVALID = new ErrorCode(40103, "登录状态无效");
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode(40104, "登录已过期");
    public static final ErrorCode USER_NOT_FOUND = new ErrorCode(40410, "用户不存在");
    /** 第三方身份在本系统无绑定关系，不可自动注册 */
    public static final ErrorCode ACCOUNT_NOT_EXISTS = new ErrorCode(40411, "账号不存在");
    /** 插入冲突时见 {@link #USER_BIND_UNIQUE_VIOLATION} */
    public static final ErrorCode USER_BIND_ALREADY_EXISTS = new ErrorCode(40910, "该用户已存在第三方绑定");
    /** 违反 user_bind 表唯一约束（具体是「每用户一条」还是「每用户每平台一条」由库表定义决定） */
    public static final ErrorCode USER_BIND_UNIQUE_VIOLATION = new ErrorCode(40911, "第三方绑定与已有数据冲突，请检查唯一约束");
    public static final ErrorCode UNSUPPORTED_SCAN_LOGIN_TYPE = new ErrorCode(40010, "不支持的扫码登录类型");
    public static final ErrorCode SCAN_LOGIN_NOT_READY = new ErrorCode(40011, "扫码登录尚未完成平台配置");
    public static final ErrorCode FEISHU_SCAN_AUTH_FAILED = new ErrorCode(40012, "飞书授权失败");
    public static final ErrorCode DINGTALK_SCAN_AUTH_FAILED = new ErrorCode(40013, "钉钉授权失败");

    private AuthErrorCodeConstants() {
    }
}
