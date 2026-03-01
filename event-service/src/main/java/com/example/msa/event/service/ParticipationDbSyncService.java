package com.example.msa.event.service;

import com.example.msa.event.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipationDbSyncService {

    private static final Logger log = LoggerFactory.getLogger(ParticipationDbSyncService.class);

    private final EventRepository eventRepository;

    public ParticipationDbSyncService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Async("participationDbSyncExecutor")
    @Transactional
    public void incrementCurrentParticipation(Long eventId) {
        try {
            eventRepository.incrementCurrentParticipation(eventId);
        } catch (Exception e) {
            log.warn("[participation-db-sync] DB 업데이트 실패 eventId={}: {}", eventId, e.getMessage());
        }
    }
}
