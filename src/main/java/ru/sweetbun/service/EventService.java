package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.DTO.EventsResponse;
import ru.sweetbun.entity.Event;

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

    private final static String URL_EVENTS = "https://kudago.com/public-api/v1.4/events/";

    @Autowired
    public EventService(RestTemplate restTemplate, CurrencyService currencyService) {
        this.restTemplate = restTemplate;
        this.currencyService = currencyService;
    }

    public CompletableFuture<List<Event>> getAvailableEvents(Double budget, String currency,
                                                             LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null) {
            dateFrom = LocalDate.now();
        }
        if (dateTo == null) {
            dateTo = LocalDate.now().plusDays(7);
        }
        String url = URL_EVENTS + "?actual_since=" + (Date.valueOf(dateFrom).getTime() / 1000L)
                + "&actual_until=" + (Date.valueOf(dateTo).getTime() / 1000L)
                + "&expand=place"
                + "&fields=id,title,price,favorites_count,place"
                + "&page_size=100"
                + "&order_by=-favorites_count";
        log.info("URL_EVENT of request: {}", url);

        CompletableFuture<List<Event>> eventsFuture = CompletableFuture.supplyAsync(() ->
                fetchEvents(url, EventsResponse.class));

        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> {
            if (currency.equalsIgnoreCase("RUB")) return budget;
            return currencyService.convertCurrencyToRUB(currency, budget);
        });

        return eventsFuture.thenCombine(convertedBudgetFuture, (events, convertedBudget) ->
                events.stream()
                .filter(event -> event.getPriceAsDouble() <= convertedBudget)
                .collect(Collectors.toList()));
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
