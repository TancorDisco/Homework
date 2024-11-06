package ru.sweetbun.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.entity.Category;
import ru.sweetbun.log.LogExecutionTime;
import ru.sweetbun.service.KudaGoService;

import java.util.Collection;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1.4/places/categories")
public class CategoryController {

    private final KudaGoService<Category> categoryService;

    @Autowired
    public CategoryController(KudaGoService<Category> categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public Collection<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return categoryService.create(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable("id") Long id) {
        return categoryService.delete(id);
    }
}
