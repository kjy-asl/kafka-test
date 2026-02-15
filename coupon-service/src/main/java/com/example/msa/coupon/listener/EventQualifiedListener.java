package com.example.msa.coupon.listener;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.EventQualifiedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.coupon.service.CouponIssuanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EventQualifiedListener {

    private static final Logger log = LoggerFactory.getLogger(EventQualifiedListener.class);
    private final CouponIssuanceService couponIssuanceService;

    public EventQualifiedListener(CouponIssuanceService couponIssuanceService) {
        this.couponIssuanceService = couponIssuanceService;
    }

    @KafkaListener(topics = KafkaTopics.EVENT_QUALIFIED_V1, groupId = "coupon-service")
    public void consume(@Payload EventQualifiedEvent event) {
        CorrelationContext.set(event.correlationId());
        log.info("[coupon-service] EventQualified 수신 order={} template={}",
                event.orderId(), event.templateCode());
        couponIssuanceService.handle(event);
    }
}
