package ru.sweetbun.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.EventDTO;
import ru.sweetbun.DTO.PlaceDTO;
import ru.sweetbun.entity.Event;
import ru.sweetbun.entity.Place;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.PlaceRepository;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, ModelMapper modelMapper) {
        this.placeRepository = placeRepository;
        this.modelMapper = modelMapper;
    }

    public Place createPlace(PlaceDTO placeDTO) {
        Place place = placeRepository.findPlaceBySlug(placeDTO.getSlug());

        if (place == null) {
            place = modelMapper.map(placeDTO, Place.class);
            return placeRepository.save(place);
        }
        return place;
    }

    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Place.class.getSimpleName(), id));
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Place updatePlace(PlaceDTO placeDTO, Long id) {
        Place place = getPlaceById(id);
        modelMapper.map(placeDTO, place);
        return placeRepository.save(place);
    }

    public void deletePlaceById(Long id) {
        placeRepository.deleteById(id);
    }
}
