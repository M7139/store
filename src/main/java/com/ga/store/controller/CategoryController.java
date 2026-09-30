package com.ga.store.controller;

import com.ga.store.dto.CategoryRequest;
import com.ga.store.dto.CategoryResponse;
import com.ga.store.model.Category;
import com.ga.store.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        Category category = categoryService.createCategory(request);

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> response = categoryService.getAllCategories()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.isActive()
                ))
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        Category category = categoryService.getCategoryById(id);

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        Category category = categoryService.updateCategory(id, request);

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return new ResponseEntity<>(
                "Category deleted successfully",
                HttpStatus.OK
        );
    }
}