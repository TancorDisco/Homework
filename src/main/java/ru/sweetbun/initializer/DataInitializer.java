package ru.sweetbun.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final KudaGoService kudaGoService;
    private final Storage<Category> categoryStorage;
    private final Storage<Location> locationStorage;

    public DataInitializer(KudaGoService kudaGoService, Storage<Category> categoryStorage, Storage<Location> locationStorage) {
        this.kudaGoService = kudaGoService;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
    }

    @LogExecutionTime
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");

        log.info("Fetching and storing categories...");
        List<Category> categories = kudaGoService.fetchAllCategories();
        categories.forEach(categoryStorage::create);
        log.info("Categories stored: {}", categories.size());

        log.info("Fetching and storing locations...");
        List<Location> locations = kudaGoService.fetchAllLocations();
        locations.forEach(locationStorage::create);
        log.info("Location stored: {}", locations.size());

        log.info("Data initialization completed successfully.");
    }
}
