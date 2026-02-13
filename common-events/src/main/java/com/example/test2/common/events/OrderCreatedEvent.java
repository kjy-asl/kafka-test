package com.example.test2.common.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable message that every service can reuse to describe the fact that a new order was created.
 *
 * <p>Because this project is meant for learning, the class purposely keeps the implementation as
 * small as possible while still demonstrating good practices:</p>
 * <ul>
 *     <li>Basic bean validation annotations document the expected constraints.</li>
 *     <li>{@link java.io.Serializable} lets us send the payload through Spring Kafka without extra converters.</li>
 *     <li>Fields are final and exposed through getters only, so downstream services cannot mutate received events.</li>
 * </ul>
 */
public record OrderCreatedEvent(
        @NotBlank String orderId,
        @NotBlank String productCode,
        @Min(1) int quantity,
        Instant createdAt
) implements Serializable {

    /**
     * Convenience factory for situations where the caller just wants to specify business fields
     * and let the event model take care of ID/timestamp creation.
     */
    public static OrderCreatedEvent of(String productCode, int quantity) {
        return new OrderCreatedEvent(UUID.randomUUID().toString(), productCode, quantity, Instant.now());
    }
}
