package com.ga.store.dto;

public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private boolean primaryImage;
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