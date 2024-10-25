package ru.sweetbun.pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sweetbun.storage.Storage;

@Component
public class StorageSaverObserver<T> implements StorageObserver<T> {

    private final Storage<T> storage;

    @Autowired
    public StorageSaverObserver(Storage<T> storage) {
        this.storage = storage;
    }

    @Override
    public void onEntityCreated(T entity) {
        storage.create(entity);
    }

    @Override
    public void onEntityUpdated(Long id, T entity) {
        storage.update(id, entity);
    }

    @Override
    public void onEntityDeleted(Long id) {
        storage.delete(id);
    }
}