package com.ga.store.controller;

import com.ga.store.dto.ProductPageResponse;
import com.ga.store.dto.ProductRequest;
import com.ga.store.dto.ProductResponse;
import com.ga.store.model.Product;
import com.ga.store.service.ProductImageService;
import com.ga.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Product browsing, searching, filtering and admin product management"
)
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create product",
            description = "Creates a new product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product already exists"
            )
    })
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
    @SecurityRequirements
    @Operation(
            summary = "Browse products",
            description = "Returns active products with optional search, category filtering, pagination and sorting."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products returned successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting information"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<ProductPageResponse> getAllProducts(
            @Parameter(
                    description = "Optional product name search",
                    example = "lavender"
            )
            @RequestParam(required = false)
            String search,

            @Parameter(
                    description = "Optional category ID filter",
                    example = "1"
            )
            @RequestParam(required = false)
            Long categoryId,

            @Parameter(
                    description = "Page number starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of products per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(
                    description = "Sort field and direction. Supported fields are name, price and createdAt",
                    example = "price,asc"
            )
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get all products for admin",
            description = "Returns all products including inactive products. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get product by ID for admin",
            description = "Returns a product including inactive products. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ProductResponse>
    getProductByIdForAdmin(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
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
    @SecurityRequirements
    @Operation(
            summary = "Get active product by ID",
            description = "Returns a specific active product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found or inactive"
            )
    })
    public ResponseEntity<ProductResponse>
    getProductById(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
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
    @SecurityRequirements
    @Operation(
            summary = "Get products by category",
            description = "Returns active products belonging to a specific category."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<List<ProductResponse>>
    getProductsByCategory(
            @Parameter(
                    description = "Category ID",
                    example = "1"
            )
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update product",
            description = "Updates an existing product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or category not found"
            )
    })
    public ResponseEntity<ProductResponse>
    updateProduct(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete product",
            description = "Deletes a product and its associated images. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<String> deleteProduct(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update product status",
            description = "Activates or deactivates a product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ProductResponse>
    updateProductStatus(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "New product active status",
                    example = "true"
            )
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