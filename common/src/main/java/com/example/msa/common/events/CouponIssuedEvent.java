package com.example.msa.common.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 쿠폰 서비스가 실제 쿠폰을 발급한 뒤 회원 서비스로 전달하는 이벤트.
 */
public record CouponIssuedEvent(
        @NotBlank String eventId,
        @NotNull Instant occurredAt,
        @NotBlank String correlationId,
        @NotNull Long couponId,
        @NotNull Long memberId,
        @NotBlank String templateCode
) implements Serializable {

    public static CouponIssuedEvent of(String correlationId,
                                       Long couponId,
                                        Long memberId,
                                       String templateCode) {
        return new CouponIssuedEvent(
                UUID.randomUUID().toString(),
                Instant.now(),
                correlationId,
                couponId,
                memberId,
                templateCode
        );
    }
}
