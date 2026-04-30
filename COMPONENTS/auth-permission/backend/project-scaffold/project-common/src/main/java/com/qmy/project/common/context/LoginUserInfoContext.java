package com.qmy.project.common.context;

import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.login.LoginUserInfo;

import java.util.Optional;

import static com.qmy.project.common.error.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class LoginUserInfoContext {

    private static final ThreadLocal<LoginUserInfo> HOLDER = new ThreadLocal<>();

    private LoginUserInfoContext() {
    }

    public static void bind(LoginUserInfo loginUserInfo) {
        HOLDER.set(loginUserInfo);
    }

    public static LoginUserInfo getLoginUserInfo() {
        return HOLDER.get();
    }

    public static LoginUserInfo requireLoginUserInfo() {
        LoginUserInfo loginUserInfo = HOLDER.get();
        if (loginUserInfo == null || loginUserInfo.getUserId() == null) {
            throw ServiceExceptionUtil.exception(UNAUTHORIZED);
        }
        return loginUserInfo;
    }

    public static Long currentUserIdOrDefault(Long defaultValue) {
        LoginUserInfo loginUserInfo = HOLDER.get();
        if (loginUserInfo == null || loginUserInfo.getUserId() == null) {
            return defaultValue;
        }
        return loginUserInfo.getUserId();
    }

    /**
     * 当前请求已绑定登录用户且 {@code userId} 非空时返回该 id，否则为空（用于审计字段：无用户则不自动填充）。
     */
    public static Optional<Long> currentUserIdOptional() {
        LoginUserInfo loginUserInfo = HOLDER.get();
        if (loginUserInfo == null || loginUserInfo.getUserId() == null) {
            return Optional.empty();
        }
        return Optional.of(loginUserInfo.getUserId());
    }

    public static void clear() {
        HOLDER.remove();
    }
}
