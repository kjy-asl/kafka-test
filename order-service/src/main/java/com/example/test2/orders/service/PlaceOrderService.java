package com.example.test2.orders.service;

import com.example.test2.common.config.KafkaTopics;
import com.example.test2.common.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * HTTP 요청으로 들어온 명령을 Kafka 이벤트로 변환하는 응용 서비스입니다.
 * 실제 서비스라면 발행 전에 DB에 주문 엔터티를 저장하겠지만, 학습 목적이므로 메시징 흐름에 집중합니다.
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
        log.info("OrderCreatedEvent 발행 완료 orderId={} product={} quantity={}",
                event.orderId(), productCode, quantity);
        return Map.of(
                "orderId", event.orderId(),
                "status", "ACCEPTED",
                "message", "요청하신 주문 이벤트를 Kafka 토픽으로 발행했습니다."
        );
    }
}
