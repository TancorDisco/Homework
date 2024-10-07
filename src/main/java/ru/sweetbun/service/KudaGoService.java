package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class KudaGoService {

    private final RestTemplate restTemplate;

    public KudaGoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Category> fetchAllCategories() {
        final String URL = "https://kudago.com/public-api/v1.4/place-categories";
        log.info("Fetching categories from KudaGo API...");
        Category[] categories = restTemplate.getForObject(URL, Category[].class);
        log.info("Categories fetched: {}", (categories != null ? categories.length : 0));
        return Arrays.asList(categories);
    }

    public List<Location> fetchAllLocations() {
        final String URL = "https://kudago.com/public-api/v1.4/locations";
        log.info("Fetching locations from KudaGo API...");
        Location[] locations = restTemplate.getForObject(URL, Location[].class);
        log.info("Locations fetched: {}", (locations != null ? locations.length : 0));
        return Arrays.asList(locations);
    }
}
