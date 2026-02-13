package com.example.test2.orders.service;

import com.example.test2.common.config.KafkaTopics;
import com.example.test2.common.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Application service that knows how to translate an incoming HTTP command into a Kafka event.
 * In a real-world system you would persist a database entity before publishing, but for study
 * purposes we skip that part and focus on the messaging flow.
 */
@Service
public class PlaceOrderService {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderService.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public PlaceOrderService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Map<String, Object> placeOrder(String productCode, int quantity) {
        var event = OrderCreatedEvent.of(productCode, quantity);
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event.orderId(), event);
        log.info("Published OrderCreatedEvent orderId={} product={} quantity={}"
                , event.orderId(), productCode, quantity);
        return Map.of(
                "orderId", event.orderId(),
                "status", "ACCEPTED",
                "message", "요청하신 주문 이벤트를 Kafka 토픽으로 발행했습니다."
        );
    }
}
