package com.example.msa.event.repository;

import com.example.msa.event.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    Optional<ProcessedEvent> findByEventTypeAndEventKey(String eventType, String eventKey);
}
