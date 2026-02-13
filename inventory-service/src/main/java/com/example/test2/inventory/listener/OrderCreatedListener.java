package com.example.test2.inventory.listener;

import com.example.test2.common.config.KafkaTopics;
import com.example.test2.common.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Simple Kafka listener that demonstrates how the inventory service can react to events emitted by
 * the order service. At the moment it only logs the message, but you can inject a repository here
 * and adjust stock levels when you are ready.
 */
@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "inventory-service")
    public void handle(@Payload OrderCreatedEvent event) {
        log.info("[inventory-service] Received order event orderId={} product={} quantity={} createdAt={}"
                , event.orderId(), event.productCode(), event.quantity(), event.createdAt());
    }
}
