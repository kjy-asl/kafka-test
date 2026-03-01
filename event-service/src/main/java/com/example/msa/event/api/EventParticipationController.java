package com.example.msa.event.api;

import com.example.msa.event.service.EventParticipationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventParticipationController {

    private final EventParticipationService participationService;

    public EventParticipationController(EventParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping("/{eventId}/participate")
    public ResponseEntity<ParticipateResponse> participate(
            @PathVariable Long eventId,
            @Valid @RequestBody ParticipateRequest request) {
        boolean success = participationService.participate(eventId, request.memberId());
        if (success) {
            return ResponseEntity.ok(ParticipateResponse.participated());
        }
        return ResponseEntity.status(409).body(ParticipateResponse.full());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
