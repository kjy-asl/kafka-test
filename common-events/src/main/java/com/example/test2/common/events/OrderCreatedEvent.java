package com.example.test2.common.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 새 주문이 생성되었다는 사실을 모든 서비스가 공통으로 표현할 수 있도록 만든 불변 이벤트 모델입니다.
 *
 * <p>학습용 프로젝트답게 구현은 가능한 한 단순하게 유지하되, 다음과 같은 모범 사례를 보여 줍니다.</p>
 * <ul>
 *     <li>Bean Validation 애노테이션으로 필드 제약 조건을 명시합니다.</li>
 *     <li>{@link java.io.Serializable}을 통해 별도 변환기 없이 Spring Kafka로 직렬화할 수 있습니다.</li>
 *     <li>모든 필드는 final이며 getter(레코드 컴포넌트)만 공개해 이벤트를 수신한 서비스에서 수정할 수 없습니다.</li>
 * </ul>
 */
public record OrderCreatedEvent(
        @NotBlank String orderId,
        @NotBlank String productCode,
        @Min(1) int quantity,
        Instant createdAt
) implements Serializable {

    /**
     * ID·타임스탬프 생성은 이벤트 모델이 맡고, 호출자는 비즈니스 필드만 넘기고 싶을 때 사용하는 팩터리입니다.
     */
    public static OrderCreatedEvent of(String productCode, int quantity) {
        return new OrderCreatedEvent(UUID.randomUUID().toString(), productCode, quantity, Instant.now());
    }
}
