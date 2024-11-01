package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import ru.sweetbun.DTO.EventsResponse;
import ru.sweetbun.entity.Event;
import ru.sweetbun.exception.CurrencyServiceUnavailableException;
import ru.sweetbun.repository.EventRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReactorEventService {

    private final RestTemplate restTemplate;

    private final EventRepository eventRepository;

    private final CurrencyService currencyService;

    private final static String URL_EVENTS = "https://kudago.com/public-api/v1.4/events/";

    @Autowired
    public ReactorEventService(RestTemplate restTemplate, EventRepository eventRepository, CurrencyService currencyService) {
        this.restTemplate = restTemplate;
        this.eventRepository = eventRepository;
        this.currencyService = currencyService;
    }

    public Mono<List<Event>> getAvailableEvents(Double budget, String currency,
                                                LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null) {
            dateFrom = LocalDate.now();
        }
        if (dateTo == null) {
            dateTo = LocalDate.now().plusDays(7);
        }
        /*String url = URL_EVENTS + "?actual_since=" + (Date.valueOf(dateFrom).getTime() / 1000L)
                + "&actual_until=" + (Date.valueOf(dateTo).getTime() / 1000L)
                + "&expand=place"
                + "&fields=id,title,price,favorites_count,place"
                + "&page_size=100"
                + "&order_by=-favorites_count";
        log.info("URL_EVENT of request: {}", url);*/

        Mono<List<Event>> eventsMono = Mono.fromCallable(eventRepository::findAll);

        Mono<Double> convertedBudgetMono = Mono.fromCallable(() -> {
            if (currency.equalsIgnoreCase("RUB")) return budget;
            return currencyService.convertCurrencyToRUB(currency, budget);
        }).onErrorResume(e -> {
            log.error("Error converting currency: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        });

        return Mono.zip(eventsMono, convertedBudgetMono)
                .flatMap(tuple -> {
                    List<Event> events = tuple.getT1();
                    Double convertedBudget = tuple.getT2();

                    List<Event> filteredEvents = events.stream()
                            .filter(event -> event.getPriceAsDouble() <= convertedBudget)
                            .collect(Collectors.toList());

                    return Mono.just(filteredEvents);
                });

    }

    public List<Event> fetchEvents(String urlEvents, Class<EventsResponse> responseType) {
        log.info("Fetching from KudaGo API...");
        EventsResponse eventResponse = restTemplate.getForObject(urlEvents, responseType);

        if (eventResponse != null && eventResponse.getResults() != null) {
            log.info("Events fetched: {}", eventResponse.getResults().size());
            return eventResponse.getResults();
        }
        return Collections.emptyList();
    }
}

