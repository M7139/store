package com.ga.store.service;

import com.ga.store.dto.CategoryRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Category;
import com.ga.store.repository.CategoryRepository;
import com.ga.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handles category creation, retrieval, updates and deletion.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * Returns all categories.
     *
     * @return all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Finds a category by ID.
     *
     * @param id category ID
     * @return matching category
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Category with id " + id + " not found"
                        ));
    }

    /**
     * Finds a category by name without case sensitivity.
     *
     * @param name category name
     * @return matching category if one exists
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name.trim());
    }

    /**
     * Checks whether a category name already exists.
     *
     * @param name category name
     * @return true if the category exists
     */
    public boolean categoryExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name.trim());
    }

    /**
     * Creates a new category while preventing duplicate names.
     *
     * @param request category information
     * @return created category
     */
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

    /**
     * Updates an existing category.
     *
     * @param id category ID
     * @param request updated category information
     * @return updated category
     */
    public Category updateCategory(Long id, CategoryRequest request) {

        Category category = getCategoryById(id);

        String categoryName = request.getName().trim();

        Optional<Category> existingCategory =
                categoryRepository.findByNameIgnoreCase(categoryName);

        if (existingCategory.isPresent()
                && !existingCategory.get().getId().equals(id)) {

            throw new InformationExistsException(
                    "Category with this name already exists"
            );
        }

        category.setName(categoryName);
        category.setDescription(request.getDescription());

        return categoryRepository.save(category);
    }

    /**
     * Deletes a category if no products are using it.
     *
     * @param id category ID
     */
    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new InformationExistsException(
                    "Category cannot be deleted because it contains products"
            );
        }

        categoryRepository.delete(category);
    }
}