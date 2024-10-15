package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.entity.Event;
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

    @GetMapping
    public CompletableFuture<List<Event>> getEvents(
            @RequestParam Double budget,
            @RequestParam String currency,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        return eventService.getAvailableEvents(budget, currency, dateFrom, dateTo);
    }

    @GetMapping("reactor")
    public Mono<List<Event>> getEventsReactor(
            @RequestParam Double budget,
            @RequestParam String currency,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        return reactorEventService.getAvailableEvents(budget, currency, dateFrom, dateTo);
    }
}
