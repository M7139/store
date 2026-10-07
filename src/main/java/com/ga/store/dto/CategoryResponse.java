package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CategoryResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Bar Soap")
    private String name;

    @Schema(example = "Traditional handcrafted soap bars")
    private String description;

    @Schema(example = "true")
    private boolean active;

    public CategoryResponse() {
    }

    public CategoryResponse(
            Long id,
            String name,
            String description,
            boolean active) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}