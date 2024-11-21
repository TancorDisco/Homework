package ru.sweetbun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.DTO.EventsResponse;
import ru.sweetbun.entity.Event;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.EventRepository;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReactorEventServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ReactorEventService reactorEventService;

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

        when(eventRepository.findAll())
                .thenReturn((List.of(event1, event2)));
        when(currencyService.convertCurrencyToRUB(anyString(), anyDouble())).thenReturn(100_000.0);

        //Act
        List<Event> result = reactorEventService.getAvailableEvents(budget, currency, null, null).block();

        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
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
        when(eventRepository.findAll())
                .thenReturn((List.of(event1, event2)));

        // Act
        List<Event> result = reactorEventService.getAvailableEvents(budget, currency, null, null).block();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("700", result.get(0).getPrice());
        verify(currencyService, times(1)).convertCurrencyToRUB(eq("USD"), eq(budget));
    }

    @Test
    public void getAvailableEvents_FetchEventsFails_ReturnsEmptyList() {
        // Arrange
        Double budget = 1000.0;
        String currency = "RUB";

        when(eventRepository.findAll())
                .thenThrow(new ResourceNotFoundException("No such events."));

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> reactorEventService.getAvailableEvents(budget, currency, null, null).block());
    }

    @Test
    public void getAvailableEvents_CurrencyConversionFails_ReturnsEmptyList() {
        // Arrange
        Double budget = 100.0;
        String currency = "USD";

        when(currencyService.convertCurrencyToRUB(eq("USD"), eq(budget)))
                .thenThrow(new RuntimeException("Conversion error"));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reactorEventService.getAvailableEvents(budget, currency, null, null).block();
        });

        assertEquals("Conversion error", exception.getMessage());
        verify(currencyService, times(1)).convertCurrencyToRUB(eq("USD"), eq(budget));
    }
}