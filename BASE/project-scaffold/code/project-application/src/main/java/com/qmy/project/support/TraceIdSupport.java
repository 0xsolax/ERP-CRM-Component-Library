package com.qmy.project.support;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class TraceIdSupport {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceIdSupport() {
    }

    public static String getOrCreateTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        return traceId;
    }

    public static void bind(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clear() {
        MDC.remove(TRACE_ID_KEY);
    }
}
