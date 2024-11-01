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
import ru.sweetbun.entity.Location;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.service.LocationService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LocationControllerIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    private LocationDTO locationDTO;

    @BeforeEach
    public void setUp() {
        locationDTO = LocationDTO.builder()
                .slug("nsk")
                .name("Новосибирск").build();
        //locationService.createLocation(locationDTO);
    }

    @Test
    @Transactional
    @Rollback
    public void createLocation_ValidData_shouldSaveInDatabase() {
        //Act
        ResponseEntity<Location> response = restTemplate.postForEntity("/api/v1.4/locations", locationDTO, Location.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Location locationSaved = locationService.getLocationBySlug(response.getBody().getSlug());
        assertThat(locationSaved).isNotNull();
        assertThat(locationSaved.getName()).isEqualTo("Новосибирск");
    }
    @Test
    public void getLocations_ValidRequest_shouldReturnFromDatabase() {
        //Arrange
        Location location = locationService.createLocation(locationDTO);

        //Act
        ParameterizedTypeReference<List<Location>> responseType = new ParameterizedTypeReference<List<Location>>() {};
        ResponseEntity<List<Location>> response = restTemplate.exchange("/api/v1.4/locations", HttpMethod.GET, null, responseType);
        log.info("Response: {}", response.getBody());

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    @Test
    public void getLocationById_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1.4/locations/99999", String.class);
        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Location not found with id: 99999");
    }

    @Test
    public void updateLocation_ValidRequest_ShouldUpdateLocation() {
        // Arrange
        Location location = locationService.createLocation(locationDTO);
        LocationDTO updatedLocationDTO = LocationDTO.builder()
                .slug("Updated slug").build();

        // Act
        restTemplate.put("/api/v1.4/locations/" + location.getId(), updatedLocationDTO);

        // Assert
        Location updatedLocation = locationService.getLocationById(location.getId());
        assertThat(updatedLocation.getSlug()).isEqualTo("Updated slug");
    }

    @Test
    public void updateLocation_InvalidId_ShouldReturnNotFound() {
        //Arrange
        LocationDTO updatedLocationDTO = LocationDTO.builder()
                .slug("Updated slug").build();

        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/locations/99999", HttpMethod.PUT, new HttpEntity<>(updatedLocationDTO), String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Location not found with id: 99999");
    }

    @Test
    public void deleteLocation_ValidId_ShouldDeleteLocation() {
        // Arrange
        Location location = locationService.createLocation(locationDTO);

        // Act
        restTemplate.delete("/api/v1.4/locations/" + location.getId());

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(location.getId()));
    }

    @Test
    public void deleteLocation_InvalidId_ShouldReturnNotFound() {
        //Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1.4/locations/99999", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        //Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Location not found with id: 99999");
    }
}
