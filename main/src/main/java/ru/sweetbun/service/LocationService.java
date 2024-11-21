package ru.sweetbun.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.DTO.LocationDTO;
import ru.sweetbun.entity.Location;
import ru.sweetbun.exception.ResourceNotFoundException;
import ru.sweetbun.pattern.HistoryManager;
import ru.sweetbun.pattern.Memento;
import ru.sweetbun.repository.LocationRepository;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final ModelMapper modelMapper;

    private final HistoryManager<Location> historyManager = new HistoryManager<>();

    @Autowired
    public LocationService(LocationRepository locationRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.modelMapper = modelMapper;
    }

    public Location createLocation(LocationDTO locationDTO) {
        Location location = locationRepository.findLocationBySlug(locationDTO.getSlug());

        if (location == null) {
            location = modelMapper.map(locationDTO, Location.class);
            historyManager.addMemento(location.saveToMemento());
            return locationRepository.save(location);
        }
        return location;
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
        historyManager.addMemento(location.saveToMemento());
        modelMapper.map(locationDTO, location);
        return locationRepository.save(location);
    }

    public void deleteLocationById(Long id) {
        Location location = getLocationById(id);
        historyManager.addMemento(location.saveToMemento());
        locationRepository.deleteById(id);
    }

    public Location getLocationBySlug(String slug) {
        return locationRepository.findLocationBySlug(slug);
    }

    public List<Memento<Location>> getHistory() {
        return historyManager.getHistory();
    }
}
