package com.hnv99.forum.core.mdc;

import org.slf4j.MDC;

/**
 * Utility class for MDC (Mapped Diagnostic Context) operations.
 */
public class MdcUtil {
    public static final String TRACE_ID_KEY = "traceId";

    public static void add(String key, String val) {
        MDC.put(key, val);
    }

    public static void addTraceId() {
        // The generation rules for traceId. The tech team provides two generation strategies,
        // you can use custom or SkyWalking; choose one in the actual project
        MDC.put(TRACE_ID_KEY, SelfTraceIdGenerator.generate());
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void reset() {
        String traceId = MDC.get(TRACE_ID_KEY);
        MDC.clear();
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clear() {
        MDC.clear();
    }
}

