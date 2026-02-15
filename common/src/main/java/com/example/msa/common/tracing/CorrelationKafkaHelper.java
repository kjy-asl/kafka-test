package com.example.msa.common.tracing;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

public final class CorrelationKafkaHelper {

    private CorrelationKafkaHelper() {
    }

    public static <K, V> void inject(ProducerRecord<K, V> record) {
        String correlationId = CorrelationContext.getOrGenerate();
        record.headers().remove(KafkaHeaders.CORRELATION_ID);
        record.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));
    }

    public static void extract(ConsumerRecord<?, ?> record) {
        String correlationId = record.headers().lastHeader(KafkaHeaders.CORRELATION_ID) != null
                ? new String(record.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value(), StandardCharsets.UTF_8)
                : null;
        if (StringUtils.hasText(correlationId)) {
            CorrelationContext.set(correlationId);
        } else {
            CorrelationContext.getOrGenerate();
        }
    }

    public static void extract(MessageHeaders headers) {
        Object header = headers.get(KafkaHeaders.CORRELATION_ID);
        if (header instanceof String id && StringUtils.hasText(id)) {
            CorrelationContext.set(id);
        } else if (header instanceof byte[] bytes) {
            CorrelationContext.set(new String(bytes, StandardCharsets.UTF_8));
        } else {
            CorrelationContext.getOrGenerate();
        }
    }
}
