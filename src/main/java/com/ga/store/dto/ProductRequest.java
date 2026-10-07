package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductRequest {

    @Schema(
            description = "Product name",
            example = "Lavender Soap"
    )
    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must be 150 characters or less")
    private String name;

    @Schema(
            description = "Product description",
            example = "Handcrafted lavender scented soap"
    )
    @Size(max = 1000, message = "Description must be 1000 characters or less")
    private String description;

    @Schema(
            description = "Product price",
            example = "2.50"
    )
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Schema(
            description = "Available stock quantity",
            example = "20"
    )
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;

    @Schema(
            description = "Category ID",
            example = "1"
    )
    @NotNull(message = "Category id is required")
    private Long categoryId;

    public ProductRequest() {
    }

    public ProductRequest(
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            Long categoryId) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}