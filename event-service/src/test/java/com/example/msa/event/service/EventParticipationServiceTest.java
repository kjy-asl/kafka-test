package com.example.msa.event.service;

import com.example.msa.common.events.EventQualifiedEvent;
import com.example.msa.event.domain.Event;
import com.example.msa.event.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock EventRepository eventRepository;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock DefaultRedisScript<Long> participationLuaScript;
    @Mock KafkaTemplate<String, EventQualifiedEvent> kafkaTemplate;
    @Mock ParticipationDbSyncService dbSyncService;
    @Mock HashOperations<String, Object, Object> hashOps;

    EventParticipationService service;

    @BeforeEach
    void setUp() {
        service = new EventParticipationService(
                eventRepository, redisTemplate, participationLuaScript, kafkaTemplate, dbSyncService);
    }

    // ── 캐시 히트: 정원 여유 → 참여 성공 ─────────────────────────────────────
    @Test
    void participate_캐시히트_성공() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList())).thenReturn(1L);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get(anyString(), eq("template"))).thenReturn("birthday-template");

        boolean result = service.participate(1L, 42L);

        assertThat(result).isTrue();
        verify(kafkaTemplate).send(any(ProducerRecord.class));
        verify(dbSyncService).incrementCurrentParticipation(1L);
        verify(eventRepository, never()).findById(any());
    }

    // ── 캐시 히트: 정원 초과 → false ────────────────────────────────────────
    @Test
    void participate_정원초과_false() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList())).thenReturn(0L);

        boolean result = service.participate(1L, 42L);

        assertThat(result).isFalse();
        verify(kafkaTemplate, never()).send(any(ProducerRecord.class));
        verify(dbSyncService, never()).incrementCurrentParticipation(any());
    }

    // ── 캐시 미스 → warm-up 후 재시도 성공 ──────────────────────────────────
    @Test
    void participate_캐시미스_warmup_후_성공() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList()))
                .thenReturn(-1L)
                .thenReturn(1L);

        Event event = buildEvent("test-template", 100, 0);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get(anyString(), eq("template"))).thenReturn("test-template");

        boolean result = service.participate(1L, 42L);

        assertThat(result).isTrue();
        verify(eventRepository).findById(1L);
        verify(hashOps).putAll(eq("event:participation:1"), any(Map.class));
        verify(kafkaTemplate).send(any(ProducerRecord.class));
        verify(dbSyncService).incrementCurrentParticipation(1L);
    }

    // ── 캐시 미스: 이벤트 없음 → EntityNotFoundException ─────────────────────
    @Test
    void participate_이벤트없음_예외() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList())).thenReturn(-1L);
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.participate(99L, 42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── warm-up 후에도 정원 초과 → false ────────────────────────────────────
    @Test
    void participate_캐시미스_warmup_후_정원초과() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList()))
                .thenReturn(-1L)
                .thenReturn(0L);

        Event event = buildEvent("test-template", 1, 1);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        boolean result = service.participate(1L, 42L);

        assertThat(result).isFalse();
        verify(kafkaTemplate, never()).send(any(ProducerRecord.class));
    }

    // ── Kafka 발행 시 templateId Redis에서 읽는지 확인 ──────────────────────
    @SuppressWarnings("unchecked")
    @Test
    void participate_성공시_templateId_Redis에서_읽음() {
        when(redisTemplate.execute(eq(participationLuaScript), anyList())).thenReturn(1L);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(hashOps.get("event:participation:5", "template")).thenReturn("promo-template");

        service.participate(5L, 7L);

        ArgumentCaptor<ProducerRecord<String, EventQualifiedEvent>> captor =
                ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        assertThat(captor.getValue().value().templateCode()).isEqualTo("promo-template");
        assertThat(captor.getValue().value().orderId()).isEqualTo(0L);
        assertThat(captor.getValue().value().memberId()).isEqualTo(7L);
    }

    // ── 헬퍼 ──────────────────────────────────────────────────────────────────
    private Event buildEvent(String templateId, int max, int current) {
        Event event = new Event();
        event.setName("Test Event");
        event.setTemplateId(templateId);
        event.setStartDate(LocalDate.now().minusDays(1));
        event.setEndDate(LocalDate.now().plusDays(30));
        event.setCreatedAt(LocalDateTime.now());
        event.setMaxParticipation(max);
        event.setCurrentParticipation(current);
        return event;
    }
}
