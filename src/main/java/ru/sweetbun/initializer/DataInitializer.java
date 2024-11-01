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
import ru.sweetbun.pattern.Command;
import ru.sweetbun.repository.LocationRepository;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DataInitializer implements ApplicationContextAware {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final ScheduledExecutorService taskScheduler;

    private final Duration scheduleDelay;

    private ApplicationContext applicationContext;

    @Autowired
    public DataInitializer(ThreadPoolTaskExecutor taskExecutor, ScheduledExecutorService taskScheduler,
                           @Value("${app.initialization.schedule-delay}") Duration scheduleDelay) {
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

        Command initializerCategoriesCommand = applicationContext.getBean(InitializerCategoriesCommand.class);
        Command initializerLocationsCommand = applicationContext.getBean(InitializerLocationsCommand.class);

        Future<?> categoryTask = taskExecutor.submit(initializerCategoriesCommand::execute);
        Future<?> locationTask = taskExecutor.submit(initializerLocationsCommand::execute);

        try {
            categoryTask.get();
            locationTask.get();
        } catch (Exception e) {
            log.error("Error occurred during data initialization", e);
        }

        log.info("Data initialization completed successfully.");
    }
}
