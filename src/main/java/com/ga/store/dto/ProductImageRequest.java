package com.ga.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private boolean primaryImage;

    @NotNull(message = "Product id is required")
    private Long productId;

    public ProductImageRequest() {
    }

    public ProductImageRequest(
            String imageUrl,
            boolean primaryImage,
            Long productId) {

        this.imageUrl = imageUrl;
        this.primaryImage = primaryImage;
        this.productId = productId;
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