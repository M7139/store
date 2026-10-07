package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProductImageResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(
            description = "URL of the uploaded product image",
            example = "/uploads/products/lavender-soap.jpg"
    )
    private String imageUrl;

    @Schema(example = "true")
    private boolean primaryImage;

    @Schema(example = "1")
    private Long productId;

    public ProductImageResponse() {
    }

    public ProductImageResponse(
            Long id,
            String imageUrl,
            boolean primaryImage,
            Long productId) {

        this.id = id;
        this.imageUrl = imageUrl;
        this.primaryImage = primaryImage;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(boolean primaryImage) {
        this.primaryImage = primaryImage;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}