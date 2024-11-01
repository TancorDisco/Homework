package ru.sweetbun.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.EventDTO;
import ru.sweetbun.entity.Event;
import ru.sweetbun.exception.RelatedEntityNotFoundException;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.service.EventService;
import ru.sweetbun.service.ReactorEventService;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1.4/events")
public class EventController {

    private final EventService eventService;

    private final ReactorEventService reactorEventService;

    @Autowired
    public EventController(EventService eventService, ReactorEventService reactorEventService) {
        this.eventService = eventService;
        this.reactorEventService = reactorEventService;
    }

    @SneakyThrows
    @GetMapping()
    public ResponseEntity<?> getAvailableEvents(
            @RequestParam(required = false) Double budget,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        List<Event> events = eventService.getAvailableEvents(budget, currency, dateFrom, dateTo).get();
        return ResponseEntity.ok(events);
    }

    @GetMapping("reactor/available")
    public Mono<List<Event>> getEventsReactor(
            @RequestParam Double budget,
            @RequestParam String currency,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        return reactorEventService.getAvailableEvents(budget, currency, dateFrom, dateTo);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.updateEvent(eventDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.ok("Event is deleted");
    }
}
