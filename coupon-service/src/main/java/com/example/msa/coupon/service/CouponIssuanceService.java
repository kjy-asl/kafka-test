package com.example.msa.coupon.service;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.CouponIssuedEvent;
import com.example.msa.common.events.EventQualifiedEvent;
import com.example.msa.common.tracing.CorrelationKafkaHelper;
import com.example.msa.coupon.domain.Coupon;
import com.example.msa.coupon.domain.CouponTemplate;
import com.example.msa.coupon.domain.ProcessedEvent;
import com.example.msa.coupon.repository.CouponRepository;
import com.example.msa.coupon.repository.CouponTemplateRepository;
import com.example.msa.coupon.repository.ProcessedEventRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CouponIssuanceService {

    private static final Logger log = LoggerFactory.getLogger(CouponIssuanceService.class);

    private final CouponTemplateRepository templateRepository;
    private final CouponRepository couponRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate;

    public CouponIssuanceService(CouponTemplateRepository templateRepository,
                                 CouponRepository couponRepository,
                                 ProcessedEventRepository processedEventRepository,
                                 KafkaTemplate<String, CouponIssuedEvent> kafkaTemplate) {
        this.templateRepository = templateRepository;
        this.couponRepository = couponRepository;
        this.processedEventRepository = processedEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void handle(EventQualifiedEvent event) {
        boolean alreadyProcessed = processedEventRepository
                .findByEventTypeAndEventKey("EVENT_QUALIFIED", event.eventId())
                .isPresent();
        if (alreadyProcessed) {
            log.info("중복 EventQualified 수신 eventId={} -> skip", event.eventId());
            return;
        }

        CouponTemplate template = templateRepository.findByTemplateCode(event.templateCode())
                .orElseThrow(() -> new IllegalArgumentException("템플릿을 찾을 수 없음"));

        Coupon coupon = new Coupon();
        coupon.setTemplate(template);
        coupon.setMemberId(event.memberId());
        coupon.setCorrelationId(event.correlationId());
        coupon.setIssuedAt(LocalDateTime.now());
        coupon.setStatus("ISSUED");

        couponRepository.save(coupon);
        processedEventRepository.save(ProcessedEvent.of("EVENT_QUALIFIED", event.eventId()));

        publishIssuedEvent(event, coupon);
    }

    private void publishIssuedEvent(EventQualifiedEvent source, Coupon coupon) {
        CouponIssuedEvent issuedEvent = CouponIssuedEvent.of(
                source.correlationId(),
                coupon.getId(),
                coupon.getMemberId(),
                coupon.getTemplate().getTemplateCode()
        );
        ProducerRecord<String, CouponIssuedEvent> record = new ProducerRecord<>(
                KafkaTopics.COUPON_ISSUED_V1,
                coupon.getMemberId().toString(),
                issuedEvent
        );
        CorrelationKafkaHelper.inject(record);
        kafkaTemplate.send(record);
        log.info("[coupon-service] CouponIssued 발행 couponId={} member={}",
                coupon.getId(), coupon.getMemberId());
    }
}
