package ru.sweetbun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.entity.Category;
import ru.sweetbun.storage.Storage;

import java.util.Collection;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1.4/places/categories")
public class CategoryController {

    private final Storage<Category> categoryStorage;

    public CategoryController(Storage<Category> categoryStorage) {
        this.categoryStorage = categoryStorage;
    }

    @GetMapping
    public Collection<Category> getAllCategories() {
        return categoryStorage.findAll().values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        return categoryStorage.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        categoryStorage.create(category);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        if (categoryStorage.findById(id).isPresent()) {
            category.setId(id);
            categoryStorage.update(id, category);
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable("id") Long id) {
        if (categoryStorage.findById(id).isPresent()) {
            categoryStorage.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
