package com.example.msa.event.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_event", uniqueConstraints = {
        @UniqueConstraint(name = "uk_processed_event", columnNames = {"event_type", "event_key"})
})
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_key", nullable = false)
    private String eventKey;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    // getters/setters
}
