package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.entity.Location;
import ru.sweetbun.exception.RelatedEntityNotFoundException;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.createLocation(locationDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(locationDTO, id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocationById(id);
        return ResponseEntity.ok("Location is deleted");
    }
}
