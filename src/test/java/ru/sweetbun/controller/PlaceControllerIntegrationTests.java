package ru.sweetbun.controller;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.DTO.PlaceDTO;
import ru.sweetbun.entity.Place;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.repository.PlaceRepository;
import ru.sweetbun.service.LocationService;
import ru.sweetbun.service.PlaceService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PlaceControllerIntegrationTests extends BaseIntegrationTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceService placeService;

    private LocationDTO locationDTO;
    private PlaceDTO placeDTO;

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
    }

    @Test
    @Transactional
    @Rollback
    public void createPlace_ValidData_shouldSaveInDatabase() {
        //Act
        ResponseEntity<Place> response = restTemplate.postForEntity("/api/v1.4/places", placeDTO, Place.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Place placeSaved = placeService.getPlaceById(response.getBody().getId());
        assertThat(placeSaved).isNotNull();
        assertThat(placeSaved.getTitle()).isEqualTo("title");
    }

    @Test
    public void getPlaces_ValidRequest_shouldReturnFromDatabase() {
        //Arrange
        placeRepository.deleteAll();
        Place place = placeService.createPlace(placeDTO);

        //Act
        ParameterizedTypeReference<List<Place>> responseType = new ParameterizedTypeReference<List<Place>>() {};
        ResponseEntity<List<Place>> response = restTemplate.exchange("/api/v1.4/places", HttpMethod.GET, null, responseType);
        log.info("Response: {}", response.getBody());

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("title");
    }

    @Test
    public void getPlaceById_ValidId_ShouldReturnPlace() {
        // Arrange
        Place place = placeService.createPlace(placeDTO);

        // Act
        ResponseEntity<Place> response = restTemplate.getForEntity("/api/v1.4/places/" + place.getId(), Place.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("title");
    }

    @Test
    public void getPlaceById_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1.4/places/99999", String.class);
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Place not found with id: 99999");
    }

    @Test
    public void updatePlace_ValidRequest_ShouldUpdatePlace() {
        // Arrange
        Place place = placeService.createPlace(placeDTO);
        PlaceDTO updatedPlaceDTO = PlaceDTO.builder()
                .title("Updated Place")
                .slug("Updated slug")
                .address("ADDRESS")
                .location(locationDTO).build();

        // Act
        restTemplate.put("/api/v1.4/places/" + place.getId(), updatedPlaceDTO);

        // Assert
        Place updatedPlace = placeService.getPlaceById(place.getId());
        assertThat(updatedPlace.getTitle()).isEqualTo("Updated Place");
        assertThat(updatedPlace.getSlug()).isEqualTo("Updated slug");
    }

    @Test
    public void updatePLace_InvalidId_ShouldReturnNotFound() {
        //Arrange
        PlaceDTO updatedPlaceDTO = PlaceDTO.builder()
                .title("Updated Place")
                .slug("Updated slug")
                .build();

        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/places/99999", HttpMethod.PUT, new HttpEntity<>(updatedPlaceDTO), String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Place not found with id: 99999");
    }

    @Test
    public void deletePlace_ValidId_ShouldDeletePlace() {
        // Arrange
        Place place = placeService.createPlace(placeDTO);

        // Act
        restTemplate.delete("/api/v1.4/places/" + place.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> placeService.getPlaceById(place.getId()));
    }

    @Test
    public void deletePlace_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/places/99999", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Place not found with id: 99999");
    }
}
