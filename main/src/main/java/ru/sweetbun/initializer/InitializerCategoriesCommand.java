package ru.sweetbun.initializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sweetbun.entity.Category;
import ru.sweetbun.pattern.Command;
import ru.sweetbun.service.KudaGoService;
import ru.sweetbun.storage.Storage;

import java.util.List;

@Slf4j
public class InitializerCategoriesCommand implements Command {

    private final KudaGoService<Category> categoryKudaGoService;
    private final Storage<Category> categoryStorage;
    private final String url;

    @Autowired
    public InitializerCategoriesCommand(KudaGoService<Category> categoryKudaGoService, Storage<Category> categoryStorage, String url) {
        this.categoryKudaGoService = categoryKudaGoService;
        this.categoryStorage = categoryStorage;
        this.url = url;
    }

    @Override
    public void execute() {
        log.info("Fetching and storing categories...");
        List<Category> categories = categoryKudaGoService.fetchAll(url, Category[].class);
        categories.forEach(categoryStorage::create);
        log.info("Categories stored: {}", categories.size());
    }
}
