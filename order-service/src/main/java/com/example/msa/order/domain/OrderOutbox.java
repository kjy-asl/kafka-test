package com.example.msa.order.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_outbox")
public class OrderOutbox {

    public enum Status { PENDING, SENT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", columnDefinition = "json", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    protected OrderOutbox() {
    }

    private OrderOutbox(String eventId, String eventType, String payload, Status status) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static OrderOutbox pending(String eventId, String eventType, String payload) {
        return new OrderOutbox(eventId, eventType, payload, Status.PENDING);
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void markSent() {
        this.status = Status.SENT;
        this.sentAt = LocalDateTime.now();
    }
}
