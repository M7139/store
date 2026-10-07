package com.ga.store.service;

import com.ga.store.dto.ProductRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Category;
import com.ga.store.model.Product;
import com.ga.store.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handles product management, public product availability,
 * searching, filtering, pagination and sorting.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;

    public ProductService(
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductImageService productImageService) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productImageService = productImageService;
    }

    /**
     * Returns all products including inactive products.
     *
     * @return all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Returns only active products.
     *
     * @return active products
     */
    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    /**
     * Searches active products with optional category filtering,
     * pagination and sorting.
     *
     * @param search optional product name search
     * @param categoryId optional category ID
     * @param page page number
     * @param size page size
     * @param sort sort field and direction
     * @return page of matching products
     */
    public Page<Product> searchActiveProducts(
            String search,
            Long categoryId,
            int page,
            int size,
            String sort) {

        if (page < 0) {
            throw new InformationExistsException(
                    "Page cannot be negative"
            );
        }

        if (size <= 0) {
            throw new InformationExistsException(
                    "Page size must be greater than 0"
            );
        }

        if (categoryId != null) {
            categoryService.getCategoryById(
                    categoryId
            );
        }

        String searchValue = null;

        if (search != null
                && !search.isBlank()) {

            searchValue =
                    search.trim();
        }

        String sortField =
                "name";

        Sort.Direction sortDirection =
                Sort.Direction.ASC;

        if (sort != null
                && !sort.isBlank()) {

            String[] sortParts =
                    sort.split(",");

            sortField =
                    sortParts[0].trim();

            if (!sortField.equals("name")
                    && !sortField.equals("price")
                    && !sortField.equals("createdAt")) {

                throw new InformationExistsException(
                        "Invalid sort field"
                );
            }

            if (sortParts.length > 1) {

                String direction =
                        sortParts[1].trim();

                if (direction.equalsIgnoreCase("desc")) {

                    sortDirection =
                            Sort.Direction.DESC;

                } else if (direction.equalsIgnoreCase("asc")) {

                    sortDirection =
                            Sort.Direction.ASC;

                } else {

                    throw new InformationExistsException(
                            "Sort direction must be asc or desc"
                    );
                }
            }
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                sortDirection,
                                sortField
                        )
                );

        return productRepository
                .searchActiveProducts(
                        searchValue,
                        categoryId,
                        pageable
                );
    }

    /**
     * Finds any product by ID.
     *
     * @param id product ID
     * @return matching product
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id "
                                        + id
                                        + " not found"
                        ));
    }

    /**
     * Finds an active product by ID.
     *
     * @param id product ID
     * @return active product
     */
    public Product getActiveProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id "
                                        + id
                                        + " not found"
                        ));
    }

    /**
     * Finds a product by name without case sensitivity.
     *
     * @param name product name
     * @return matching product if present
     */
    public Optional<Product> getProductByName(
            String name) {

        return productRepository
                .findByNameIgnoreCase(
                        name.trim()
                );
    }

    /**
     * Returns products belonging to a category.
     *
     * @param categoryId category ID
     * @return products in category
     */
    public List<Product> getProductsByCategoryId(
            Long categoryId) {

        return productRepository
                .findByCategoryId(
                        categoryId
                );
    }

    /**
     * Returns active products belonging to a category.
     *
     * @param categoryId category ID
     * @return active products in category
     */
    public List<Product> getActiveProductsByCategoryId(
            Long categoryId) {

        categoryService.getCategoryById(
                categoryId
        );

        return productRepository
                .findByCategoryIdAndActiveTrue(
                        categoryId
                );
    }

    /**
     * Checks whether a product name already exists.
     *
     * @param name product name
     * @return true if the product exists
     */
    public boolean productExists(
            String name) {

        return productRepository
                .existsByNameIgnoreCase(
                        name.trim()
                );
    }

    /**
     * Creates a new product.
     *
     * @param request product information
     * @return created product
     */
    public Product createProduct(
            ProductRequest request) {

        String productName =
                request.getName().trim();

        if (productRepository
                .existsByNameIgnoreCase(
                        productName
                )) {

            throw new InformationExistsException(
                    "Product with this name already exists"
            );
        }

        Category category =
                categoryService.getCategoryById(
                        request.getCategoryId()
                );

        Product product =
                new Product(
                        productName,
                        request.getDescription(),
                        request.getPrice(),
                        request.getStockQuantity(),
                        category
                );

        return productRepository.save(
                product
        );
    }

    /**
     * Updates an existing product.
     *
     * @param id product ID
     * @param request updated product information
     * @return updated product
     */
    public Product updateProduct(
            Long id,
            ProductRequest request) {

        Product product =
                getProductById(id);

        String productName =
                request.getName().trim();

        Optional<Product> existingProduct =
                productRepository
                        .findByNameIgnoreCase(
                                productName
                        );

        if (existingProduct.isPresent()
                && !existingProduct
                .get()
                .getId()
                .equals(id)) {

            throw new InformationExistsException(
                    "Product with this name already exists"
            );
        }

        Category category =
                categoryService.getCategoryById(
                        request.getCategoryId()
                );

        product.setName(
                productName
        );

        product.setDescription(
                request.getDescription()
        );

        product.setPrice(
                request.getPrice()
        );

        product.setStockQuantity(
                request.getStockQuantity()
        );

        product.setCategory(
                category
        );

        return productRepository.save(
                product
        );
    }

    /**
     * Deletes a product and its associated uploaded images.
     *
     * @param id product ID
     */
    public void deleteProduct(
            Long id) {

        Product product =
                getProductById(id);

        productImageService
                .deleteImagesByProductId(
                        id
                );

        productRepository.delete(
                product
        );
    }

    /**
     * Activates or deactivates a product.
     *
     * @param id product ID
     * @param active requested active state
     * @return updated product
     */
    public Product updateProductStatus(
            Long id,
            boolean active) {

        Product product =
                getProductById(id);

        product.setActive(
                active
        );

        return productRepository.save(
                product
        );
    }
}