package com.example.msa.event.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "template_id", nullable = false)
    private String templateId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "max_participation", nullable = false)
    private int maxParticipation = 100;

    @Column(name = "current_participation", nullable = false)
    private int currentParticipation = 0;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventCondition> conditions = new ArrayList<>();

    public void addCondition(EventCondition condition) {
        condition.setEvent(this);
        this.conditions.add(condition);
    }

    public Long getId() {
        return id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<EventCondition> getConditions() {
        return conditions;
    }

    public boolean isActive(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate))
                && (date.isEqual(endDate) || date.isBefore(endDate));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getMaxParticipation() {
        return maxParticipation;
    }

    public int getCurrentParticipation() {
        return currentParticipation;
    }

    public void setMaxParticipation(int maxParticipation) {
        this.maxParticipation = maxParticipation;
    }

    public void setCurrentParticipation(int currentParticipation) {
        this.currentParticipation = currentParticipation;
    }
}
