package com.example.msa.common.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 주문 서비스에서 발행하는 V1 이벤트 모델. 멱등성과 추적을 위해 eventId/occurredAt/correlationId를 공통으로 포함합니다.
 */
public record OrderCreatedEvent(
        @NotBlank String eventId,
        @NotNull Instant occurredAt,
        @NotBlank String correlationId,
        @NotNull Long orderId,
        @NotNull Long memberId,
        @NotNull BigDecimal totalAmount,
        @NotNull List<OrderLine> items
) implements Serializable {

    public static OrderCreatedEvent of(String correlationId,
                                       Long orderId,
                                       Long memberId,
                                       BigDecimal totalAmount,
                                       List<OrderLine> items) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                correlationId,
                orderId,
                memberId,
                totalAmount,
                items
        );
    }

    /** 주문 항목 페이로드. */
    public record OrderLine(
            @NotBlank String productId,
            @Min(1) int quantity,
            @NotNull BigDecimal unitPrice
    ) implements Serializable {
    }
}
