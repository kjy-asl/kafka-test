package com.example.test2.orders.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for the /api/orders endpoint. Keeping it as a simple record makes serialization and
 * validation straightforward while keeping the example compact.
 */
public record CreateOrderRequest(
        @NotBlank(message = "상품 코드를 입력하세요") String productCode,
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다") int quantity
) {
}
