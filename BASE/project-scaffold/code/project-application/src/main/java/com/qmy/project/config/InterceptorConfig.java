package com.qmy.project.config;

import com.qmy.project.core.auth.model.AuthSettings;
import com.qmy.project.core.auth.manager.AuthManager;
import com.qmy.project.interceptor.AuthTokenInterceptor;
import com.qmy.project.interceptor.TraceInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 集中注册 MVC 拦截器：先 Trace，再鉴权（除匿名路径外必须带有效 Token）。
 *
 * @author AI Coding
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthSettings authSettings;

    private final AuthManager authManager;

    /**
     * 不经过 JWT 校验的路径（登录、按域名查租户、OpenAPI 文档等）。
     */
    private static final String[] ANONYMOUS_PATH_PATTERNS = {
            "/external/**",
            "/sysUser/loginByPassword",
            "/sysUser/loginByScan",
            "/qiaoMoYun/tenant/getTenantId",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error",
    };

    private TraceInterceptor initTraceInterceptor() {
        return new TraceInterceptor();
    }

    private AuthTokenInterceptor initAuthInterceptor() {
        return new AuthTokenInterceptor(authSettings, authManager);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initTraceInterceptor())
                .addPathPatterns("/**")
                .order(0);
        registry.addInterceptor(initAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(ANONYMOUS_PATH_PATTERNS)
                .order(1);
    }
}
