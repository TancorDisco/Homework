package ru.sweetbun.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Identifiable;
import ru.sweetbun.storage.Storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class KudaGoService<T extends Identifiable> {

    private final RestTemplate restTemplate;
    private final Storage<T> storage;

    @Autowired
    public KudaGoService(RestTemplate restTemplate, Storage<T> categoryStorage) {
        this.restTemplate = restTemplate;
        this.storage = categoryStorage;
    }

    public List<T> fetchAll(String URL, Class<T[]> responseType) {
        log.info("Fetching from KudaGo API...");
        T[] entities = restTemplate.getForObject(URL, responseType);
        if (entities != null) {
            log.info("{} fetched: {}", responseType, entities.length);
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
        storage.create(entity);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<T> update(Long id, T entity) {
        if (storage.findById(id).isPresent()) {
            entity.setId(id);
            storage.update(id, entity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<T> delete(Long id) {
        if (storage.findById(id).isPresent()) {
            storage.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
