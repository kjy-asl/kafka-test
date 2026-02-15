package com.example.msa.event.service;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.EventQualifiedEvent;
import com.example.msa.common.events.OrderCreatedEvent;
import com.example.msa.common.tracing.CorrelationKafkaHelper;
import com.example.msa.event.client.MemberServiceClient;
import com.example.msa.event.domain.Event;
import com.example.msa.event.domain.EventCondition;
import com.example.msa.event.domain.EventConditionType;
import com.example.msa.event.domain.ProcessedEvent;
import com.example.msa.event.repository.EventRepository;
import com.example.msa.event.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventQualificationService {

    private static final Logger log = LoggerFactory.getLogger(EventQualificationService.class);

    private final EventRepository eventRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, EventQualifiedEvent> kafkaTemplate;
    private final MemberServiceClient memberServiceClient;

    public EventQualificationService(EventRepository eventRepository,
                                     ProcessedEventRepository processedEventRepository,
                                     KafkaTemplate<String, EventQualifiedEvent> kafkaTemplate,
                                     MemberServiceClient memberServiceClient) {
        this.eventRepository = eventRepository;
        this.processedEventRepository = processedEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.memberServiceClient = memberServiceClient;
    }

    @Transactional
    public void evaluate(OrderCreatedEvent orderEvent) {
        boolean already = processedEventRepository
                .findByEventTypeAndEventKey("ORDER_CREATED", orderEvent.eventId())
                .isPresent();
        if (already) {
            log.info("중복 이벤트 감지 orderEventId={} -> skip", orderEvent.eventId());
            return;
        }

        List<Event> events = eventRepository.findAll();
        var profile = memberServiceClient.fetchProfile(orderEvent.memberId());
        LocalDate today = orderEvent.occurredAt().atZone(memberServiceClient.zone()).toLocalDate();

        events.stream()
                .filter(ev -> ev.isActive(today))
                .filter(ev -> meetsConditions(ev, orderEvent, profile))
                .forEach(ev -> publishQualified(ev, orderEvent));

        processedEventRepository.save(ProcessedEvent.of(null, "ORDER_CREATED", orderEvent.eventId()));
    }

    private void publishQualified(Event event, OrderCreatedEvent orderEvent) {
        EventQualifiedEvent qualified = EventQualifiedEvent.of(
                orderEvent.correlationId(),
                orderEvent.orderId(),
                orderEvent.memberId(),
                event.getTemplateId(),
                "조건 충족"
        );
        var record = new org.apache.kafka.clients.producer.ProducerRecord<>(
                KafkaTopics.EVENT_QUALIFIED_V1,
                orderEvent.orderId().toString(),
                qualified
        );
        CorrelationKafkaHelper.inject(record);
        kafkaTemplate.send(record);
        log.info("[event-service] EventQualified 발행 order={} template={}"
                , orderEvent.orderId(), event.getTemplateId());
    }

    private boolean meetsConditions(Event event,
                                    OrderCreatedEvent orderEvent,
                                    MemberServiceClient.MemberProfile profile) {
        return event.getConditions().stream().allMatch(condition -> switch (condition.type()) {
            case PRODUCT -> orderEvent.items().stream()
                    .anyMatch(item -> item.productId().equals(condition.getConditionValue()));
            case MIN_AMOUNT -> orderEvent.totalAmount()
                    .compareTo(memberServiceClient.parseAmount(condition.getConditionValue())) >= 0;
            case BIRTHDAY -> profile != null && memberServiceClient.matchesBirthday(profile, condition.getConditionValue());
        });
    }
}
