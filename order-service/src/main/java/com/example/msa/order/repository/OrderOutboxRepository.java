package com.example.msa.order.repository;

import com.example.msa.order.domain.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, String> {
}
