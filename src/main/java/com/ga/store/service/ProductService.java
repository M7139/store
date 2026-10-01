package com.ga.store.service;

import com.ga.store.dto.ProductRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Category;
import com.ga.store.model.Product;
import com.ga.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(
            ProductRepository productRepository,
            CategoryService categoryService) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id " + id + " not found"
                        ));
    }

    public Optional<Product> getProductByName(String name) {
        return productRepository.findByNameIgnoreCase(name.trim());
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public boolean productExists(String name) {
        return productRepository.existsByNameIgnoreCase(name.trim());
    }

    public Product createProduct(ProductRequest request) {

        String productName = request.getName().trim();

        if (productRepository.existsByNameIgnoreCase(productName)) {
            throw new InformationExistsException(
                    "Product with this name already exists"
            );
        }

        Category category =
                categoryService.getCategoryById(request.getCategoryId());

        Product product = new Product(
                productName,
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                category
        );

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request) {

        Product product = getProductById(id);

        String productName = request.getName().trim();

        Optional<Product> existingProduct =
                productRepository.findByNameIgnoreCase(productName);

        if (existingProduct.isPresent()
                && !existingProduct.get().getId().equals(id)) {

            throw new InformationExistsException(
                    "Product with this name already exists"
            );
        }

        Category category =
                categoryService.getCategoryById(request.getCategoryId());

        product.setName(productName);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }
}