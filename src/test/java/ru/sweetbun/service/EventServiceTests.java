package ru.sweetbun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.DTO.EventsResponse;
import ru.sweetbun.entity.Event;
import ru.sweetbun.exception.CurrencyNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EventServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAvailableEvents_ValidDataRUB_ReturnFilteredEvents() {
        //Arrange
        double budget = 1000.0;
        String currency = "RUB";
        Event event1 = Event.builder().price("500").build();
        Event event2 = Event.builder().price("700").build();

        when(restTemplate.getForObject(anyString(), eq(EventsResponse.class)))
                .thenReturn(new EventsResponse(List.of(event1, event2)));
        when(currencyService.convertCurrencyToRUB(anyString(), anyDouble())).thenReturn(100_000.0);

        //Act
        List<Event> result = eventService.getAvailableEvents(budget, currency, null, null).join();

        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(EventsResponse.class));
        verify(currencyService, never()).convertCurrencyToRUB(anyString(), anyDouble());
    }

    @Test
    public void getAvailableEvents_CurrencyConversionNeeded_ReturnsFilteredEvents() {
        //Arrange
        Event event1 = Event.builder().price("5000").build();
        Event event2 = Event.builder().price("700").build();
        Double budget = 10.0;
        String currency = "USD";

        when(currencyService.convertCurrencyToRUB(eq("USD"), eq(budget))).thenReturn(1000.0);
        when(restTemplate.getForObject(anyString(), eq(EventsResponse.class)))
                .thenReturn(new EventsResponse(List.of(event1, event2)));

        // Act
        List<Event> result = eventService.getAvailableEvents(budget, currency, null, null).join();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("700", result.get(0).getPrice());
        verify(currencyService, times(1)).convertCurrencyToRUB(eq("USD"), eq(budget));
    }

    @Test
    public void getAvailableEvents_FetchEventsFails_ReturnsEmptyList() {
        // Assert
        Double budget = 1000.0;
        String currency = "RUB";

        when(restTemplate.getForObject(anyString(), eq(EventsResponse.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act
        List<Event> result = eventService.getAvailableEvents(budget, currency, null, null).join();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(EventsResponse.class));
    }

    @Test
    public void getAvailableEvents_CurrencyConversionFails_ReturnsEmptyList() {
        // Arrange
        Double budget = 100.0;
        String currency = "USD";

        when(currencyService.convertCurrencyToRUB(eq("USD"), eq(budget)))
                .thenThrow(new CurrencyNotFoundException("Conversion error"));

        // Act
        List<Event> result = eventService.getAvailableEvents(budget, currency, null, null).join();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(currencyService, times(1)).convertCurrencyToRUB(eq("USD"), eq(budget));
    }
}