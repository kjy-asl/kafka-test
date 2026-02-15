package com.example.msa.order.service;

import com.example.msa.common.config.KafkaTopics;
import com.example.msa.common.events.OrderCreatedEvent;
import com.example.msa.common.tracing.CorrelationContext;
import com.example.msa.common.tracing.CorrelationKafkaHelper;
import com.example.msa.order.api.CreateOrderRequest;
import com.example.msa.order.api.CreateOrderResponse;
import com.example.msa.order.domain.Order;
import com.example.msa.order.domain.OrderItem;
import com.example.msa.order.domain.OrderOutbox;
import com.example.msa.order.repository.OrderOutboxRepository;
import com.example.msa.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlaceOrderService {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderService.class);

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PlaceOrderService(OrderRepository orderRepository,
                             OrderOutboxRepository orderOutboxRepository,
                             KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
                             ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.kafkaTemplate = kafkaTemplate;
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

        orderOutboxRepository.save(OrderOutbox.pending(event.eventId(), OrderCreatedEvent.class.getSimpleName(), writePayload(event)));

        return new CreateOrderResponse(orderNumber, order.getStatus(), "주문이 접수되었습니다.");
    }

    private String writePayload(OrderCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("이벤트 직렬화 실패", e);
        }
    }

    @Scheduled(fixedDelayString = "${order.outbox.poll-interval:1000}")
    @Transactional
    public void publishOutbox() {
        List<OrderOutbox> pending = orderOutboxRepository.findTop10ByStatusOrderByCreatedAtAsc(OrderOutbox.Status.PENDING);
        pending.forEach(outbox -> {
            try {
                OrderCreatedEvent event = objectMapper.readValue(outbox.getPayload(), OrderCreatedEvent.class);
                ProducerRecord<String, OrderCreatedEvent> record = new ProducerRecord<>(
                        KafkaTopics.ORDER_CREATED_V1,
                        event.orderId().toString(),
                        event
                );
                CorrelationContext.set(event.correlationId());
                CorrelationKafkaHelper.inject(record);
                kafkaTemplate.send(record);
                outbox.markSent();
            } catch (Exception e) {
                log.error("Outbox publish 실패 eventId={}", outbox.getEventId(), e);
            }
        });
    }
}
