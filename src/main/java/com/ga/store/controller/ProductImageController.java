package com.ga.store.controller;

import com.ga.store.dto.ProductImageResponse;
import com.ga.store.model.ProductImage;
import com.ga.store.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@Tag(
        name = "Product Images",
        description = "Product image viewing and admin image management"
)
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(
            ProductImageService productImageService) {

        this.productImageService = productImageService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Upload product image",
            description = "Uploads an image for a product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product image uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image file"
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
    public ResponseEntity<ProductImageResponse> uploadProductImage(
            @Parameter(
                    description = "Image file to upload"
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @RequestParam Long productId,

            @Parameter(
                    description = "Whether this image should be the product's primary image",
                    example = "true"
            )
            @RequestParam(defaultValue = "false")
            boolean primaryImage) {

        ProductImage productImage =
                productImageService.uploadProductImage(
                        file,
                        productId,
                        primaryImage
                );

        ProductImageResponse response =
                new ProductImageResponse(
                        productImage.getId(),
                        productImage.getImageUrl(),
                        productImage.isPrimaryImage(),
                        productImage.getProduct().getId()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/product/{productId}")
    @SecurityRequirements
    @Operation(
            summary = "Get product images",
            description = "Returns the images for an active product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product images returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<List<ProductImageResponse>> getImagesByProduct(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @PathVariable Long productId) {

        List<ProductImageResponse> response =
                productImageService
                        .getActiveProductImagesByProductId(
                                productId
                        )
                        .stream()
                        .map(productImage ->
                                new ProductImageResponse(
                                        productImage.getId(),
                                        productImage.getImageUrl(),
                                        productImage.isPrimaryImage(),
                                        productImage.getProduct().getId()
                                ))
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get all product images for admin",
            description = "Returns all images for a product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product images returned successfully"
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
    public ResponseEntity<List<ProductImageResponse>> getImagesByProductForAdmin(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @PathVariable Long productId) {

        List<ProductImageResponse> response =
                productImageService
                        .getImagesByProductId(
                                productId
                        )
                        .stream()
                        .map(productImage ->
                                new ProductImageResponse(
                                        productImage.getId(),
                                        productImage.getImageUrl(),
                                        productImage.isPrimaryImage(),
                                        productImage.getProduct().getId()
                                ))
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/{id}/primary")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Set primary product image",
            description = "Sets an image as the primary image for its product. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Primary image updated successfully"
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
                    description = "Product image not found"
            )
    })
    public ResponseEntity<ProductImageResponse> setPrimaryImage(
            @Parameter(
                    description = "Product image ID",
                    example = "1"
            )
            @PathVariable Long id) {

        ProductImage productImage =
                productImageService.setPrimaryImage(
                        id
                );

        ProductImageResponse response =
                new ProductImageResponse(
                        productImage.getId(),
                        productImage.getImageUrl(),
                        productImage.isPrimaryImage(),
                        productImage.getProduct().getId()
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
            summary = "Delete product image",
            description = "Deletes a product image and removes its uploaded file. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product image deleted successfully"
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
                    description = "Product image not found"
            )
    })
    public ResponseEntity<String> deleteProductImage(
            @Parameter(
                    description = "Product image ID",
                    example = "1"
            )
            @PathVariable Long id) {

        productImageService.deleteProductImage(
                id
        );

        return new ResponseEntity<>(
                "Product image deleted successfully",
                HttpStatus.OK
        );
    }
}