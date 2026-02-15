package com.example.msa.member.listener;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.CouponIssuedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.member.service.MemberCouponService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CouponIssuedListener {

    private final MemberCouponService memberCouponService;

    public CouponIssuedListener(MemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    @KafkaListener(topics = KafkaTopics.COUPON_ISSUED_V1, groupId = "member-service")
    public void consume(@Payload CouponIssuedEvent event) {
        CorrelationContext.set(event.correlationId());
        memberCouponService.assignCoupon(event);
    }
}
