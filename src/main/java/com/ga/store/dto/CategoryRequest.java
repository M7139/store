package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @Schema(
            description = "Category name",
            example = "Bar Soap"
    )
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be 100 characters or less")
    private String name;

    @Schema(
            description = "Category description",
            example = "Traditional handcrafted soap bars"
    )
    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    public CategoryRequest() {
    }

    public CategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
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
}