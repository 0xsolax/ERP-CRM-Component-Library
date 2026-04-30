package com.qmy.project.interceptor;

import com.qmy.project.support.TraceIdSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 与 {@link TraceIdSupport} 配合，在 MVC 链路中写入 TraceId（响应头 + MDC）。
 *
 * @author AI Coding
 */
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = TraceIdSupport.getOrCreateTraceId(request.getHeader(TraceIdSupport.TRACE_ID_HEADER));
        TraceIdSupport.bind(traceId);
        response.setHeader(TraceIdSupport.TRACE_ID_HEADER, traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        TraceIdSupport.clear();
    }
}
