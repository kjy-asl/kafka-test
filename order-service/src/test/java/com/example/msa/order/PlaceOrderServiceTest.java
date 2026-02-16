package com.example.msa.order;

import com.example.msa.order.api.CreateOrderRequest;
import com.example.msa.order.domain.Order;
import com.example.msa.order.domain.OrderOutbox;
import com.example.msa.order.repository.OrderOutboxRepository;
import com.example.msa.order.repository.OrderRepository;
import com.example.msa.order.service.PlaceOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderOutboxRepository orderOutboxRepository;

    @Mock
    KafkaTemplate<String, ?> kafkaTemplate;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    PlaceOrderService placeOrderService;

    @Test
    void 주문_생성시_outbox에_이벤트가_적재된다() throws JsonProcessingException {
        CreateOrderRequest request = new CreateOrderRequest(
                1L,
                List.of(new CreateOrderRequest.OrderItemRequest("BOOK-001", 2, BigDecimal.valueOf(10000)))
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.recalculateTotal();
            return order;
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        placeOrderService.placeOrder(request);

        verify(orderRepository).save(any(Order.class));

        ArgumentCaptor<OrderOutbox> captor = ArgumentCaptor.forClass(OrderOutbox.class);
        verify(orderOutboxRepository).save(captor.capture());

        OrderOutbox outbox = captor.getValue();
        assertThat(outbox.getStatus()).isEqualTo(OrderOutbox.Status.PENDING);
        assertThat(outbox.getPayload()).isEqualTo("{}");
    }
}
