package com.ga.store.controller;

import com.ga.store.dto.ProductPageResponse;
import com.ga.store.dto.ProductRequest;
import com.ga.store.dto.ProductResponse;
import com.ga.store.model.Product;
import com.ga.store.service.ProductImageService;
import com.ga.store.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(
            ProductService productService,
            ProductImageService productImageService) {

        this.productService = productService;
        this.productImageService = productImageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product =
                productService.createProduct(
                        request
                );

        ProductResponse response =
                createProductResponse(
                        product
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse> getAllProducts(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "name,asc")
            String sort) {

        Page<Product> productPage =
                productService.searchActiveProducts(
                        search,
                        categoryId,
                        page,
                        size,
                        sort
                );

        List<ProductResponse> products =
                productPage
                        .getContent()
                        .stream()
                        .map(this::createProductResponse)
                        .toList();

        ProductPageResponse response =
                new ProductPageResponse(
                        products,
                        productPage.getNumber(),
                        productPage.getSize(),
                        productPage.getTotalElements(),
                        productPage.getTotalPages()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponse>>
    getAllProductsForAdmin() {

        List<ProductResponse> response =
                productService
                        .getAllProducts()
                        .stream()
                        .map(this::createProductResponse)
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse>
    getProductByIdForAdmin(
            @PathVariable Long id) {

        Product product =
                productService.getProductById(
                        id
                );

        ProductResponse response =
                createProductResponse(
                        product
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse>
    getProductById(
            @PathVariable Long id) {

        Product product =
                productService
                        .getActiveProductById(
                                id
                        );

        ProductResponse response =
                createProductResponse(
                        product
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>>
    getProductsByCategory(
            @PathVariable Long categoryId) {

        List<ProductResponse> response =
                productService
                        .getActiveProductsByCategoryId(
                                categoryId
                        )
                        .stream()
                        .map(this::createProductResponse)
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse>
    updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product =
                productService.updateProduct(
                        id,
                        request
                );

        ProductResponse response =
                createProductResponse(
                        product
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(
                id
        );

        return new ResponseEntity<>(
                "Product deleted successfully",
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse>
    updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        Product product =
                productService
                        .updateProductStatus(
                                id,
                                active
                        );

        ProductResponse response =
                createProductResponse(
                        product
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    private ProductResponse createProductResponse(
            Product product) {

        String primaryImageUrl =
                productImageService
                        .getPrimaryImageUrl(
                                product.getId()
                        );

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.isActive(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                primaryImageUrl
        );
    }
}