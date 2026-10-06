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

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

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

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id "
                                        + id
                                        + " not found"
                        ));
    }

    public Product getActiveProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id "
                                        + id
                                        + " not found"
                        ));
    }

    public Optional<Product> getProductByName(
            String name) {

        return productRepository
                .findByNameIgnoreCase(
                        name.trim()
                );
    }

    public List<Product> getProductsByCategoryId(
            Long categoryId) {

        return productRepository
                .findByCategoryId(
                        categoryId
                );
    }

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

    public boolean productExists(
            String name) {

        return productRepository
                .existsByNameIgnoreCase(
                        name.trim()
                );
    }

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