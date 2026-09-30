package com.ga.store.service;

import com.ga.store.dto.CategoryRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Category;
import com.ga.store.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Category with id " + id + " not found"
                        ));
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public boolean categoryExists(String name) {
        return categoryRepository.existsByName(name);
    }

    public Category createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new InformationExistsException(
                    "Category with this name already exists"
            );
        }

        Category category = new Category(
                request.getName(),
                request.getDescription()
        );

        return categoryRepository.save(category);
    }
}