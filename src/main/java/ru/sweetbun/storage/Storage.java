package ru.sweetbun.storage;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Storage<T> {
    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public void create(T entity) {
        Long id = idGenerator.incrementAndGet();
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        storage.put(id, entity);
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Map<Long, T> findAll() {
        return storage;
    }

    public void update(Long id, T entity) {
        storage.put(id, entity);
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}
