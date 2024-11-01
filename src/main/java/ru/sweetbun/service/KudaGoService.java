package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Identifiable;
import ru.sweetbun.pattern.HistoryManager;
import ru.sweetbun.pattern.Memento;
import ru.sweetbun.storage.Storage;
import ru.sweetbun.pattern.StorageObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class KudaGoService<T extends Identifiable> {

    private final RestTemplate restTemplate;
    private final List<StorageObserver<T>> observers = new ArrayList<>();
    private final Storage<T> storage;
    private final HistoryManager<T> historyManager = new HistoryManager<>();

    @Autowired
    public KudaGoService(RestTemplate restTemplate, Storage<T> storage) {
        this.restTemplate = restTemplate;
        this.storage = storage;
    }

    public void addObserver(StorageObserver<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(StorageObserver<T> observer) {
        observers.remove(observer);
    }

    private void notifyEntityCreated(T entity) {
        observers.forEach(observer -> observer.onEntityCreated(entity));
    }

    private void notifyEntityUpdated(Long id, T entity) {
        observers.forEach(observer -> observer.onEntityUpdated(id, entity));
    }

    private void notifyEntityDeleted(Long id) {
        observers.forEach(observer -> observer.onEntityDeleted(id));
    }

    public List<T> fetchAll(String URL, Class<T[]> responseType) {
        log.info("Fetching from KudaGo API...");
        T[] entities = restTemplate.getForObject(URL, responseType);
        if (entities != null) {
            log.info("{} fetched: {}", responseType, entities.length);
            Arrays.stream(entities).forEach(this::notifyEntityCreated);
            return Arrays.asList(entities);
        }
        return null;
    }

    public Collection<T> findAll() {
        return storage.findAll().values();
    }

    public ResponseEntity<T> findById(Long id) {
        return storage.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<T> create(T entity) {
        historyManager.addMemento(entity.saveToMemento());
        notifyEntityCreated(entity);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<T> update(Long id, T entity) {
        if (storage.findById(id).isPresent()) {
            historyManager.addMemento(entity.saveToMemento());
            entity.setId(id);
            notifyEntityUpdated(id, entity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<T> delete(Long id) {
        if (storage.findById(id).isPresent()) {
            T entity = storage.findById(id).get();
            historyManager.addMemento(entity.saveToMemento());
            notifyEntityDeleted(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public List<Memento<T>> getHistory() {
        return historyManager.getHistory();
    }
}