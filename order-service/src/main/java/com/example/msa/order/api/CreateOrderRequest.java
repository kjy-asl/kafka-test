package com.example.msa.order.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotNull Long memberId,
        @NotEmpty List<@Valid OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotBlank String productId,
            @Min(1) int quantity,
            @NotNull BigDecimal unitPrice
    ) {}
}
