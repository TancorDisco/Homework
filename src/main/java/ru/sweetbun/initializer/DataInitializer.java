package ru.sweetbun.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static String URL_CATEGORY = "https://kudago.com/public-api/v1.4/place-categories";
    private final static String URL_LOCATION = "https://kudago.com/public-api/v1.4/locations";

    @Autowired
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
        List<Category> categories = kudaGoService.fetchAll(URL_CATEGORY, Category[].class);
        categories.forEach(categoryStorage::create);
        log.info("Categories stored: {}", categories.size());

        log.info("Fetching and storing locations...");
        List<Location> locations = kudaGoService.fetchAll(URL_LOCATION, Location[].class);
        locations.forEach(locationStorage::create);
        log.info("Location stored: {}", locations.size());

        log.info("Data initialization completed successfully.");
    }
}
