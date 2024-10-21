package ru.sweetbun.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.entity.Location;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public LocationService(LocationRepository locationRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.modelMapper = modelMapper;
    }

    public Location createLocation(LocationDTO locationDTO) {
        Location location = modelMapper.map(locationDTO, Location.class);
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Location.class.getSimpleName(), id));
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location updateLocation(LocationDTO locationDTO, Long id) {
        Location location = getLocationById(id);
        modelMapper.map(locationDTO, location);
        return locationRepository.save(location);
    }

    public void deleteLocationById(Long id) {
        locationRepository.deleteById(id);
    }
}
