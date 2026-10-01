package com.ga.store.controller;

import com.ga.store.dto.ProductImageRequest;
import com.ga.store.dto.ProductImageResponse;
import com.ga.store.model.ProductImage;
import com.ga.store.service.ProductImageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(
            ProductImageService productImageService) {

        this.productImageService = productImageService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageResponse> createProductImage(
            @Valid @RequestBody ProductImageRequest request) {

        ProductImage productImage =
                productImageService.createProductImage(request);

        ProductImageResponse response = new ProductImageResponse(
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

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductImageResponse> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "false") boolean primaryImage) {

        ProductImage productImage =
                productImageService.uploadProductImage(
                        file,
                        productId,
                        primaryImage
                );

        ProductImageResponse response = new ProductImageResponse(
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
    public ResponseEntity<List<ProductImageResponse>> getImagesByProduct(
            @PathVariable Long productId) {

        List<ProductImageResponse> response =
                productImageService.getImagesByProductId(productId)
                        .stream()
                        .map(productImage -> new ProductImageResponse(
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
}