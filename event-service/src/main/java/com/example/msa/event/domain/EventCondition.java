package com.example.msa.event.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "event_conditions")
public class EventCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    @Column(name = "condition_value", nullable = false)
    private String conditionValue;

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public EventConditionType type() {
        return EventConditionType.from(conditionType);
    }
}
