package com.example.test2.orders.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * /api/orders 엔드포인트에서 사용하는 요청 모델입니다. Java Record 형태로 유지하면 직렬화/검증이 단순해지고
 * 예제 코드도 간결하게 유지할 수 있습니다.
 */
public record CreateOrderRequest(
        @NotBlank(message = "상품 코드를 입력하세요") String productCode,
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다") int quantity
) {
}
