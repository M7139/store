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

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> response = productService.getActiveProducts()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.isActive(),
                        product.getCategory().getId(),
                        product.getCategory().getName()
                ))
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        Product product = productService.getProductById(id);

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

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId) {

        List<ProductResponse> response =
                productService.getActiveProductsByCategoryId(categoryId)
                        .stream()
                        .map(product -> new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice(),
                                product.getStockQuantity(),
                                product.isActive(),
                                product.getCategory().getId(),
                                product.getCategory().getName()
                        ))
                        .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.updateProduct(id, request);

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

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return new ResponseEntity<>(
                "Product deleted successfully",
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        Product product = productService.updateProductStatus(id, active);

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

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}