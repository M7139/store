package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}