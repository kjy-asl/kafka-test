package com.example.test2.common.config;

/**
 * Shared topic names. Having a single source of truth makes refactoring much safer when there are
 * multiple services participating in the same event stream.
 */
public final class KafkaTopics {

    public static final String ORDER_CREATED = "orders.created";

    private KafkaTopics() {
    }
}
