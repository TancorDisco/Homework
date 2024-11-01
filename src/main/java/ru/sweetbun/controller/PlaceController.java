package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.DTO.PlaceDTO;
import ru.sweetbun.entity.Place;
import ru.sweetbun.service.PlaceService;

@RestController
@RequestMapping("/api/v1.4/places")
public class PlaceController {

    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.getPlaceById(id));
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody PlaceDTO placeDTO) {
        return ResponseEntity.ok(placeService.createPlace(placeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody PlaceDTO placeDTO) {
        return ResponseEntity.ok(placeService.updatePlace(placeDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlace(@PathVariable Long id) {
        placeService.deletePlaceById(id);
        return ResponseEntity.ok("Place is deleted");
    }
}
