package ru.sweetbun.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import ru.sweetbun.entity.Category;
import ru.sweetbun.entity.Location;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DataInitializer implements ApplicationContextAware {

    private final KudaGoService kudaGoService;
    private final Storage<Category> categoryStorage;
    private final Storage<Location> locationStorage;
    private final static String URL_CATEGORY = "https://kudago.com/public-api/v1.4/place-categories";
    private final static String URL_LOCATION = "https://kudago.com/public-api/v1.4/locations";

    private final ThreadPoolTaskExecutor taskExecutor;
    private final ScheduledExecutorService taskScheduler;

    private final Duration scheduleDelay;

    private ApplicationContext applicationContext;

    @Autowired
    public DataInitializer(KudaGoService kudaGoService, Storage<Category> categoryStorage, Storage<Location> locationStorage,
                           ThreadPoolTaskExecutor taskExecutor, ScheduledExecutorService taskScheduler,
                           @Value("${app.initialization.schedule-delay}") Duration scheduleDelay) {
        this.kudaGoService = kudaGoService;
        this.categoryStorage = categoryStorage;
        this.locationStorage = locationStorage;
        this.scheduleDelay = scheduleDelay;
        this.taskExecutor = taskExecutor;
        this.taskScheduler = taskScheduler;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        taskScheduler.scheduleAtFixedRate(() -> {
            DataInitializer self = applicationContext.getBean(DataInitializer.class);
            taskExecutor.submit(self::initializeData);
        }, 0, scheduleDelay.toMinutes(), TimeUnit.MINUTES);
    }

    @LogExecutionTime
    public void initializeData() {
        log.info("Starting parallel data initialization...");

        Future<?> categoryTask = taskExecutor.submit(() -> {
            log.info("Fetching and storing categories...");
            List<Category> categories = kudaGoService.fetchAll(URL_CATEGORY, Category[].class);
            categories.forEach(categoryStorage::create);
            log.info("Categories stored: {}", categories.size());
        });

        Future<?> locationTask = taskExecutor.submit(() -> {
            log.info("Fetching and storing locations...");
            List<Location> locations = kudaGoService.fetchAll(URL_LOCATION, Location[].class);
            locations.forEach(locationStorage::create);
            log.info("Locations stored: {}", locations.size());
        });

        try {
            categoryTask.get();
            locationTask.get();
        } catch (Exception e) {
            log.error("Error occurred during data initialization", e);
        }

        log.info("Data initialization completed successfully.");
    }
}
