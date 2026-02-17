package com.example.msa.event.api;

import com.example.msa.event.domain.Event;
import com.example.msa.event.domain.EventCondition;
import com.example.msa.event.repository.EventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
public class EventSeedController {

    private final EventRepository eventRepository;

    public EventSeedController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping("/seed")
    public ResponseEntity<?> seed() {
        eventRepository.deleteAll();

        Event birthdayEvent = createEvent("BIRTHDAY-BONUS", "birthday-template", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        birthdayEvent.addCondition(condition("BIRTHDAY", LocalDate.now().toString().substring(5)));

        Event productEvent = createEvent("PRODUCT-BOOK", "book-template", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        productEvent.addCondition(condition("PRODUCT", "BOOK-001"));
        productEvent.addCondition(condition("MIN_AMOUNT", "15000"));

        eventRepository.save(birthdayEvent);
        eventRepository.save(productEvent);

        return ResponseEntity.ok("seeded");
    }

    private Event createEvent(String name, String templateId, LocalDate start, LocalDate end) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(name + " description");
        event.setTemplateId(templateId);
        event.setStartDate(start);
        event.setEndDate(end);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }

    private EventCondition condition(String type, String value) {
        EventCondition condition = new EventCondition();
        condition.setConditionType(type);
        condition.setConditionValue(value);
        return condition;
    }
}
