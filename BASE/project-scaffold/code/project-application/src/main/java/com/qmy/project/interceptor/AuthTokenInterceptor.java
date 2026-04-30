package com.qmy.project.interceptor;

import com.qmy.project.core.auth.model.AuthSettings;
import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.core.auth.manager.AuthManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.qmy.project.common.error.GlobalErrorCodeConstants.UNAUTHORIZED;

/**
 * 在 {@link com.qmy.project.config.InterceptorConfig} 中注册；仅对非匿名路径生效，必须携带有效 Token。
 *
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@RequiredArgsConstructor
public class AuthTokenInterceptor implements HandlerInterceptor {

    private final AuthSettings authSettings;

    private final AuthManager authManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String accessToken = resolveAccessToken(request);
        if (accessToken == null || accessToken.isBlank()) {
            throw ServiceExceptionUtil.exception(UNAUTHORIZED);
        }
        LoginUserInfo loginUserInfo = authManager.resolveLoginUser(accessToken);
        LoginUserInfoContext.bind(loginUserInfo);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginUserInfoContext.clear();
    }

    private String resolveAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(authSettings.getJwt().getHeaderName());
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        return headerValue.trim();
    }
}
