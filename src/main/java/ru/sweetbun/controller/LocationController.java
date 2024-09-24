package ru.sweetbun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.entity.Location;
import ru.sweetbun.storage.Storage;

import java.util.Collection;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1.4/locations")
public class LocationController {
    private final Storage<Location> locationStorage;

    public LocationController(Storage<Location> locationStorage) {
        this.locationStorage = locationStorage;
    }

    @GetMapping
    public Collection<Location> getAllLocations() {
        return locationStorage.findAll().values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return locationStorage.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        locationStorage.create(location);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        if (locationStorage.findById(id).isPresent()) {
            location.setId(id);
            locationStorage.update(id, location);
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Location> deleteLocation(@PathVariable Long id) {
        if (locationStorage.findById(id).isPresent()) {
            locationStorage.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
