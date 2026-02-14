package com.example.test2.inventory.listener;

import com.example.test2.common.config.KafkaTopics;
import com.example.test2.common.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 주문 서비스가 발행한 이벤트에 재고 서비스가 어떻게 반응하는지 보여 주는 단순한 Kafka 리스너입니다.
 * 지금은 로그만 남기지만, 나중에 저장소를 주입해 실제 재고 차감 로직을 연결할 수 있습니다.
 */
@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "inventory-service")
    public void handle(@Payload OrderCreatedEvent event) {
        log.info("[inventory-service] 주문 이벤트 수신 orderId={} product={} quantity={} createdAt={}",
                event.orderId(), event.productCode(), event.quantity(), event.createdAt());
    }
}
