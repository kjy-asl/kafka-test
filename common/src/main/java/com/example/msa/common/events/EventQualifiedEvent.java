package com.example.msa.common.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 이벤트 서비스가 자격 충족 시 발행하는 메시지. 쿠폰 서비스가 구독합니다.
 */
public record EventQualifiedEvent(
        @NotBlank String eventId,
        @NotNull Instant occurredAt,
        @NotBlank String correlationId,
        @NotNull Long orderId,
        @NotNull Long memberId,
        @NotBlank String templateCode,
        @NotBlank String reason
) implements Serializable {

    public static EventQualifiedEvent of(String correlationId,
                                         Long orderId,
                                         Long memberId,
                                         String templateCode,
                                         String reason) {
        return new EventQualifiedEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                correlationId,
                orderId,
                memberId,
                templateCode,
                reason
        );
    }
}
