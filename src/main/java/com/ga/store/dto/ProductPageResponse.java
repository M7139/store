package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ProductPageResponse {

    @Schema(description = "Products on the current page")
    private List<ProductResponse> content;

    @Schema(example = "0")
    private int page;

    @Schema(example = "10")
    private int size;

    @Schema(example = "12")
    private long totalElements;

    @Schema(example = "2")
    private int totalPages;

    public ProductPageResponse() {
    }

    public ProductPageResponse(
            List<ProductResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages) {

        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<ProductResponse> getContent() {
        return content;
    }

    public void setContent(
            List<ProductResponse> content) {

        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(
            int page) {

        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(
            int size) {

        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(
            long totalElements) {

        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(
            int totalPages) {

        this.totalPages = totalPages;
    }
}