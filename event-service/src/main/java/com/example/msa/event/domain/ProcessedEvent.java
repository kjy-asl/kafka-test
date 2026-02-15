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

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_key", nullable = false)
    private String eventKey;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    protected ProcessedEvent() {
    }

    private ProcessedEvent(Long eventId, String eventType, String eventKey) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.processedAt = LocalDateTime.now();
    }

    public static ProcessedEvent of(Long eventId, String eventType, String eventKey) {
        return new ProcessedEvent(eventId, eventType, eventKey);
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventKey() {
        return eventKey;
    }
}
