package com.example.msa.coupon.listener;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.OrderCreatedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED_V1, groupId = "coupon-service")
    public void handle(@Payload OrderCreatedEvent event) {
        CorrelationContext.set(event.correlationId());
        log.info("[coupon-service] 주문 이벤트 수신 orderId={} member={} amount={} items={}",
                event.orderId(), event.memberId(), event.totalAmount(), event.items().size());
    }
}
