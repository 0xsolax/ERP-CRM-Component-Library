package com.qmy.project.alert;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class FeishuAlertMessageBuilder {

    private static final String UNKNOWN = "unknown";
    private static final int MAX_FIELD_LENGTH = 4000;

    private FeishuAlertMessageBuilder() {
    }

    public static AlertMessage build(String applicationName,
                                     String traceId,
                                     Exception exception,
                                     HttpServletRequest request,
                                     String triggerUserLine) {
        String safeApplicationName = defaultText(applicationName);
        String safeTraceId = defaultText(traceId);

        List<String> lines = new ArrayList<>();
        lines.add("TraceId: " + safeTraceId);
        lines.add("触发用户: " + defaultText(triggerUserLine));

        if (request != null) {
            lines.add("IP: " + resolveClientIp(request));
            lines.add("URL: " + buildRequestUrl(request));
            lines.add("方法: " + defaultText(request.getMethod()));
        }

        lines.add("异常类型: " + exception.getClass().getName());
        lines.add("异常信息: ");
        String msg = exception.getMessage();
        if (msg == null || msg.isEmpty()) {
            lines.add(UNKNOWN);
        } else {
            for (String part : msg.split("\r\n|\n|\r", -1)) {
                lines.add(truncate(part));
            }
        }

        StackTraceElement primaryFrame = primaryFrameOf(exception);
        if (primaryFrame != null) {
            lines.add("报错位置: " + formatErrorLocation(primaryFrame));
        }

        String title = "【" + safeApplicationName + " 服务异常告警 】";
        return new AlertMessage(title, lines);
    }

    private static String formatErrorLocation(StackTraceElement frame) {
        String path = frame.getClassName().replace('.', '/') + ".java";
        int line = frame.getLineNumber();
        return line > 0 ? path + ":" + line : path;
    }

    private static String buildRequestUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(defaultText(request.getRequestURL() == null ? null : request.getRequestURL().toString()));
        String queryString = normalize(request.getQueryString());
        if (!queryString.isBlank()) {
            builder.append('?').append(queryString);
        }
        return truncate(builder.toString());
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String headerName : headerNames) {
            String value = normalize(request.getHeader(headerName));
            if (!value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                return truncate(value.split(",")[0].trim());
            }
        }
        return truncate(defaultText(request.getRemoteAddr()));
    }

    private static StackTraceElement primaryFrameOf(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        StackTraceElement[] stackTrace = root.getStackTrace();
        if (stackTrace == null || stackTrace.length == 0) {
            return null;
        }
        for (StackTraceElement frame : stackTrace) {
            if (frame.getClassName().startsWith("com.qmy.project")) {
                return frame;
            }
        }
        return stackTrace[0];
    }

    private static String defaultText(String value) {
        String normalized = normalize(value);
        return normalized.isBlank() ? UNKNOWN : normalized;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .trim();
    }

    private static String truncate(String value) {
        String normalized = Objects.requireNonNullElse(value, UNKNOWN);
        if (normalized.length() <= MAX_FIELD_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_FIELD_LENGTH - 3) + "...";
    }

    @Getter
    public static final class AlertMessage {

        private final String title;

        private final List<String> lines;

        private AlertMessage(String title, List<String> lines) {
            this.title = title;
            this.lines = List.copyOf(lines);
        }
    }
}
