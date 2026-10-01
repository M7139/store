package com.ga.store.controller;

import com.ga.store.dto.ProductRequest;
import com.ga.store.dto.ProductResponse;
import com.ga.store.model.Product;
import com.ga.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.createProduct(request);

        ProductResponse response = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.isActive(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}