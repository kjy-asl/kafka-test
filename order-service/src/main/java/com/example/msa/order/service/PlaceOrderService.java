package com.example.msa.order.service;

import com.example.msa.common.events.OrderCreatedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.order.api.CreateOrderRequest;
import com.example.msa.order.api.CreateOrderResponse;
import com.example.msa.order.domain.Order;
import com.example.msa.order.domain.OrderItem;
import com.example.msa.order.domain.OrderOutbox;
import com.example.msa.order.repository.OrderOutboxRepository;
import com.example.msa.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PlaceOrderService {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    public PlaceOrderService(OrderRepository orderRepository,
                             OrderOutboxRepository orderOutboxRepository,
                             ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public CreateOrderResponse placeOrder(CreateOrderRequest request) {
        String correlationId = CorrelationContext.getOrGenerate();
        String orderNumber = UUID.randomUUID().toString();

        Order order = new Order(orderNumber, request.memberId(), "CREATED");
        request.items().forEach(item -> order.addItem(new OrderItem(item.productId(), item.quantity(), item.unitPrice())));
        order.recalculateTotal();
        orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.of(
                correlationId,
                order.getId(),
                order.getMemberId(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(i -> new OrderCreatedEvent.OrderLine(i.getProductId(), i.getQuantity(), i.getUnitPrice()))
                        .toList()
        );

        orderOutboxRepository.save(OrderOutbox.of(
                event.eventId(),
                Order.class.getSimpleName(),
                order.getId().toString(),
                OrderCreatedEvent.class.getSimpleName(),
                writePayload(event)
        ));

        return new CreateOrderResponse(orderNumber, order.getStatus(), "주문이 접수되었습니다.");
    }

    private String writePayload(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("이벤트 직렬화 실패", e);
        }
    }

    /**
     * Outbox → Kafka 전송은 Debezium Outbox Connector가 담당합니다.
     * (docker-compose.yml 의 kafka-connect + connectors/order-outbox-connector.json)
     */
    private void noopPublishHint() {
        // CDC 기반이므로 애플리케이션 레벨에서 추가 배치 처리가 필요 없습니다.
    }
}
