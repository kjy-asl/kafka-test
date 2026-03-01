package com.example.msa.event.service;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.EventQualifiedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.common.tracing.CorrelationKafkaHelper;
import com.example.msa.event.domain.Event;
import com.example.msa.event.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventParticipationService {

    private static final Logger log = LoggerFactory.getLogger(EventParticipationService.class);
    private static final String KEY_PREFIX = "event:participation:";

    private final EventRepository eventRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> participationLuaScript;
    private final KafkaTemplate<String, EventQualifiedEvent> kafkaTemplate;
    private final ParticipationDbSyncService dbSyncService;

    public EventParticipationService(EventRepository eventRepository,
                                     RedisTemplate<String, String> redisTemplate,
                                     DefaultRedisScript<Long> participationLuaScript,
                                     KafkaTemplate<String, EventQualifiedEvent> kafkaTemplate,
                                     ParticipationDbSyncService dbSyncService) {
        this.eventRepository = eventRepository;
        this.redisTemplate = redisTemplate;
        this.participationLuaScript = participationLuaScript;
        this.kafkaTemplate = kafkaTemplate;
        this.dbSyncService = dbSyncService;
    }

    /**
     * @return true 참여 성공, false 정원 초과
     * @throws EntityNotFoundException 이벤트 없음
     */
    public boolean participate(Long eventId, Long memberId) {
        String key = KEY_PREFIX + eventId;

        Long result = executeLua(key);

        if (result == -1L) {
            warmUpRedis(key, eventId);
            result = executeLua(key);
        }

        if (result == 1L) {
            String templateId = (String) redisTemplate.opsForHash().get(key, "template");
            String correlationId = CorrelationContext.getOrGenerate();
            publishKafkaEvent(eventId, memberId, templateId, correlationId);
            dbSyncService.incrementCurrentParticipation(eventId);
            return true;
        }

        return false;
    }

    private Long executeLua(String key) {
        Long result = redisTemplate.execute(participationLuaScript, List.of(key));
        return result != null ? result : -1L;
    }

    private void warmUpRedis(String key, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

        redisTemplate.opsForHash().putAll(key, Map.of(
                "max", String.valueOf(event.getMaxParticipation()),
                "current", String.valueOf(event.getCurrentParticipation()),
                "template", event.getTemplateId()
        ));
        log.info("[participation] Redis warm-up eventId={} max={} current={}",
                eventId, event.getMaxParticipation(), event.getCurrentParticipation());
    }

    private void publishKafkaEvent(Long eventId, Long memberId, String templateId, String correlationId) {
        EventQualifiedEvent qualified = EventQualifiedEvent.of(
                correlationId,
                0L,
                memberId,
                templateId,
                "이벤트 직접 참여"
        );
        ProducerRecord<String, EventQualifiedEvent> record = new ProducerRecord<>(
                KafkaTopics.EVENT_QUALIFIED_V1,
                eventId.toString(),
                qualified
        );
        CorrelationKafkaHelper.inject(record);
        kafkaTemplate.send(record);
        log.info("[participation] EventQualified 발행 eventId={} memberId={} template={}", eventId, memberId, templateId);
    }
}
