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
        return categoryRepository.findByNameIgnoreCase(name.trim());
    }

    public boolean categoryExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name.trim());
    }

    public Category createCategory(CategoryRequest request) {

        String categoryName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new InformationExistsException(
                    "Category with this name already exists"
            );
        }

        Category category = new Category(
                categoryName,
                request.getDescription()
        );

        return categoryRepository.save(category);
    }
}