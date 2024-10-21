package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.entity.Location;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.service.LocationService;

import java.util.Collection;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1.4/locations")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<?> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.createLocation(locationDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(locationDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocationById(id);
        return ResponseEntity.ok("Location is deleted");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {;
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
