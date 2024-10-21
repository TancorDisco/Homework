package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.DTO.EventDTO;
import ru.sweetbun.DTO.EventsResponse;
import ru.sweetbun.DTO.PlaceDTO;
import ru.sweetbun.entity.Event;
import ru.sweetbun.entity.Place;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.EventRepository;
import ru.sweetbun.repository.PlaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    private final RestTemplate restTemplate;
    private final CurrencyService currencyService;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final PlaceService placeService;
    private final PlaceRepository placeRepository;
    private final static String URL_EVENTS = "https://kudago.com/public-api/v1.4/events/";

    @Autowired
    public EventService(RestTemplate restTemplate, CurrencyService currencyService, EventRepository eventRepository,
                        ModelMapper modelMapper, PlaceService placeService, PlaceRepository placeRepository) {
        this.restTemplate = restTemplate;
        this.currencyService = currencyService;
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.placeService = placeService;
        this.placeRepository = placeRepository;
    }

    public CompletableFuture<List<Event>> getAvailableEvents(Double budget, String currency,
                                                             LocalDate dateFrom, LocalDate dateTo) {
        log.info("Starting event retrieval for budget: {} {}, from: {}, to: {}", budget, currency, dateFrom, dateTo);

        if (dateFrom == null) {
            dateFrom = LocalDate.now();
            log.debug("Date from not provided, defaulting to today: {}", dateFrom);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now().plusDays(7);
            log.debug("Date to not provided, defaulting to one week ahead: {}", dateTo);
        }

        CompletableFuture<List<Event>> eventsFuture = CompletableFuture.supplyAsync(() -> {
            log.info("Fetching events from KudaGo service...");
            List<Event> events = getAllEvents();
            log.info("Successfully fetched {} events", events.size());
            return events;
        });

        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> {
            log.info("Converting budget: {} {}", budget, currency);
            if (currency.equalsIgnoreCase("RUB")) {
                log.debug("Currency is RUB, no conversion needed");
                return budget;
            } else {
                try {
                    Double convertedBudget = currencyService.convertCurrencyToRUB(currency, budget);
                    log.info("Converted budget: {} RUB", convertedBudget);
                    return convertedBudget;
                } catch (Exception e) {
                    log.error("Error occurred during currency conversion: {}", e.getMessage());
                    throw new RuntimeException("Currency conversion failed", e);
                }
            }
        });

        return eventsFuture.thenCombine(convertedBudgetFuture, (events, convertedBudget) -> {
            log.info("Filtering events by budget: {} RUB", convertedBudget);
            List<Event> filteredEvents = events.stream()
                    .filter(event -> event.getPriceAsDouble() <= convertedBudget)
                    .collect(Collectors.toList());
            log.info("{} events are within the user's budget", filteredEvents.size());
            return filteredEvents;
        }).exceptionally(ex -> {
            log.error("Error occurred while retrieving events: {}", ex.getMessage());
            return Collections.emptyList();
        });
    }

    public List<Event> fetchEvents(String urlEvents, Class<EventsResponse> responseType) {
        log.info("Fetching events from KudaGo API using URL: {}", urlEvents);
        try {
            EventsResponse eventResponse = restTemplate.getForObject(urlEvents, responseType);
            if (eventResponse != null && eventResponse.getResults() != null) {
                log.info("Fetched {} events from KudaGo API", eventResponse.getResults().size());
                return eventResponse.getResults();
            }
            log.warn("No events found in the response");
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to fetch events from KudaGo API: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void createEvent(EventDTO eventDTO) {
        PlaceDTO placeDTO = eventDTO.getPlace();
        Place place = placeService.createPlace(placeDTO);
        Event event = modelMapper.map(eventDTO, Event.class);

        event.setPlace(place);
        place.getEvents().add(event);
        eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Event.class.getSimpleName(), id));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event updateEvent(EventDTO eventDTO, Long id) {
        Event event = getEventById(id);
        modelMapper.map(eventDTO, event);
        return eventRepository.save(event);
    }

    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }
}
