package com.example.msa.order.api;

public record CreateOrderResponse(
        String orderNumber,
        String status,
        String message
) {
}
