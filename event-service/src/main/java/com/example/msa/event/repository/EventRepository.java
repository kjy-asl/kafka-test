package com.example.msa.event.repository;

import com.example.msa.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Modifying
    @Query("UPDATE Event e SET e.currentParticipation = e.currentParticipation + 1 WHERE e.id = :eventId")
    void incrementCurrentParticipation(@Param("eventId") Long eventId);
}
