package com.example.msa.order.repository;

import com.example.msa.order.domain.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, Long> {
    List<OrderOutbox> findTop10ByStatusOrderByCreatedAtAsc(OrderOutbox.Status status);
}
