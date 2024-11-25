package ru.sweetbun.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sweetbun.entity.Location;
import ru.sweetbun.pattern.Command;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.service.KudaGoService;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class InitializerLocationsCommand implements Command {

    private final KudaGoService<Location> locationKudaGoService;
    private final LocationRepository locationRepository;
    private final String url;

    @Autowired
    public InitializerLocationsCommand(KudaGoService<Location> locationKudaGoService, LocationRepository locationRepository, String url) {
        this.locationKudaGoService = locationKudaGoService;
        this.locationRepository = locationRepository;
        this.url = url;
    }

    @Override
    public void execute() {
        log.info("Fetching and storing locations...");
        List<Location> locations = locationKudaGoService.fetchAll(url, Location[].class);
        AtomicLong i = new AtomicLong(1L);
        locations.forEach(location -> {
            Location oldLocation = locationRepository.findLocationBySlug(location.getSlug());
            if (oldLocation == null) {
                location.setId(i.getAndIncrement());
                locationRepository.save(location);
            }
        });
        log.info("Locations stored: {}", locations.size());
    }
}
