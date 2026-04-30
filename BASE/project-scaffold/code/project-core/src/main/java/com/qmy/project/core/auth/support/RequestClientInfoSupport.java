package com.qmy.project.core.auth.support;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 从当前请求解析客户端 IP、User-Agent（无 Web 上下文时返回空对象）。
 */
public final class RequestClientInfoSupport {

    private RequestClientInfoSupport() {
    }

    public static ClientInfo current() {
        ClientInfo clientInfo = new ClientInfo();
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return clientInfo;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        clientInfo.setClientIp(resolveClientIp(request));
        clientInfo.setUserAgent(request.getHeader("User-Agent"));
        return clientInfo;
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String[] headerNames = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP" };
        for (String headerName : headerNames) {
            String value = request.getHeader(headerName);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                return value.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    @Data
    public static class ClientInfo {

        private String clientIp;

        private String userAgent;
    }
}
