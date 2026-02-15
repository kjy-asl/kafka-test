package com.example.msa.common.tracing;

import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

public final class CorrelationContext {

    public static final String HEADER_NAME = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static String getOrGenerate() {
        return Optional.ofNullable(HOLDER.get())
                .orElseGet(() -> {
                    String generated = UUID.randomUUID().toString();
                    set(generated);
                    return generated;
                });
    }

    public static void set(String value) {
        HOLDER.set(value);
        MDC.put(MDC_KEY, value);
    }

    public static void clear() {
        HOLDER.remove();
        MDC.remove(MDC_KEY);
    }
}
