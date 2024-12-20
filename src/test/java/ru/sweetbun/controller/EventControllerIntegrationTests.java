package ru.sweetbun.controller;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.sweetbun.DTO.EventDTO;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.DTO.PlaceDTO;
import ru.sweetbun.entity.Event;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.EventRepository;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.repository.PlaceRepository;
import ru.sweetbun.service.EventService;
import ru.sweetbun.service.LocationService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EventControllerIntegrationTests extends BaseIntegrationTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    private LocationDTO locationDTO;
    private PlaceDTO placeDTO;
    private EventDTO eventDTO;
    private Event event;

    @BeforeEach
    public void setUp() {
        locationDTO = LocationDTO.builder()
                .slug("msk")
                .name("Москва").build();
        locationService.createLocation(locationDTO);

        placeDTO = PlaceDTO.builder()
                .title("title")
                .slug("slug")
                .address("address")
                .location(locationDTO).build();

        eventDTO = EventDTO.builder()
                .title("Test Event")
                .price("100")
                .date(LocalDate.now())
                .place(placeDTO).build();
    }

    @Test
    @Transactional
    void createEvent_ValidData_shouldSaveInDatabase() {
        //Act
        ResponseEntity<Event> response = restTemplate.postForEntity("/api/v1.4/events", eventDTO, Event.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Event savedEvent = eventService.getEventById(response.getBody().getId());
        assertThat(savedEvent).isNotNull();
        assertThat(savedEvent.getTitle()).isEqualTo("Test Event");
    }

    @Test
    void getEventById_ValidId_ShouldReturnEvent() {
        // Arrange
        event = eventService.createEvent(eventDTO);

        // Act
        ResponseEntity<Event> response = restTemplate.getForEntity("/api/v1.4/events/" + event.getId(), Event.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Event");
    }

    @Test
    void getEventById_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1.4/events/99999", String.class);
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Event not found with id: 99999");
    }

    @Transactional
    @Test
    void updateEvent_InvalidId_ShouldReturnNotFound() {
        //Arrange
        EventDTO updatedEventDTO = EventDTO.builder()
                .title("Updated Event")
                .price("200")
                .date(LocalDate.now())
                .build();

        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/events/99999", HttpMethod.PUT, new HttpEntity<>(updatedEventDTO), String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Event not found with id: 99999");
    }

    @Transactional
    @Test
    void deleteEvent_ValidId_ShouldDeleteEvent() {
        // Arrange
        event = eventService.createEvent(eventDTO);

        // Act
        restTemplate.delete("/api/v1.4/events/" + 100L);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(100L));
    }

    @Transactional
    @Test
    void deleteEvent_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/events/99999", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Event not found with id: 99999");
    }
}
