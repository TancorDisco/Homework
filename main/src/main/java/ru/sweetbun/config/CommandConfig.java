package ru.sweetbun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.initializer.InitializerCategoriesCommand;
import ru.sweetbun.initializer.InitializerLocationsCommand;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

@Configuration
public class CommandConfig {

    @Value("${command.api-url.categories}")
    private String categoryUrl;

    @Value("${command.api-url.locations}")
    private String locationUrl;

    @Bean
    public InitializerCategoriesCommand initializerCategoriesCommand(KudaGoService<Category> categoryKudaGoService, Storage<Category> categoryStorage) {
        return new InitializerCategoriesCommand(categoryKudaGoService, categoryStorage, categoryUrl);
    }

    @Bean
    public InitializerLocationsCommand initializerLocationsCommand(KudaGoService<Location> locationKudaGoService, LocationRepository locationRepository) {
        return new InitializerLocationsCommand(locationKudaGoService, locationRepository, locationUrl);
    }
}
