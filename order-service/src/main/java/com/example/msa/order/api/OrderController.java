package com.example.msa.order.api;

import com.example.msa.order.service.PlaceOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderService placeOrderService;

    public OrderController(PlaceOrderService placeOrderService) {
        this.placeOrderService = placeOrderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Validated @RequestBody CreateOrderRequest request) {
        return ResponseEntity.accepted().body(placeOrderService.placeOrder(request));
    }
}
