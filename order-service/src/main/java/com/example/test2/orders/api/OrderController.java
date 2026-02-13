package com.example.test2.orders.api;

import com.example.test2.orders.service.PlaceOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 생성을 위한 REST 진입점입니다. HTTP 관련 처리는 이 클래스에서 끝내고, 실제 비즈니스 로직은
 * {@link PlaceOrderService}로 위임해 책임 경계를 명확하게 유지합니다.
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
