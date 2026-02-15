package com.example.msa.event.listener;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.OrderCreatedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.event.service.EventQualificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);
    private final EventQualificationService qualificationService;

    public OrderCreatedListener(EventQualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED_V1,
            groupId = "event-service",
            containerFactory = "orderCreatedListenerFactory")
    public void consume(@Payload OrderCreatedEvent event) {
        CorrelationContext.set(event.correlationId());
        log.info("[event-service] 주문 이벤트 수신 orderId={} member={} amount={}",
                event.orderId(), event.memberId(), event.totalAmount());
        qualificationService.evaluate(event);
    }
}
