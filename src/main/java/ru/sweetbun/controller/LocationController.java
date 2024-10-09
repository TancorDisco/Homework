package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.entity.Location;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.service.KudaGoService;

import java.util.Collection;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1.4/locations")
public class LocationController {
    private final KudaGoService<Location> locationService;

    @Autowired
    public LocationController(KudaGoService<Location> locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public Collection<Location> getAllLocations() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return locationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return locationService.create(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        return locationService.update(id, location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Location> deleteLocation(@PathVariable Long id) {
        return locationService.delete(id);
    }
}
