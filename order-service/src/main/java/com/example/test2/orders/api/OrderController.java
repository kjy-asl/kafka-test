package com.example.test2.orders.api;

import com.example.test2.orders.service.PlaceOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entrypoint used by clients (or other services) to create orders. Keeping HTTP concerns here
 * and delegating everything else to {@link PlaceOrderService} keeps the boundary clean.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderService placeOrderService;

    public OrderController(PlaceOrderService placeOrderService) {
        this.placeOrderService = placeOrderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Validated @RequestBody CreateOrderRequest request) {
        var response = placeOrderService.placeOrder(request.productCode(), request.quantity());
        return ResponseEntity.accepted().body(response);
    }
}
