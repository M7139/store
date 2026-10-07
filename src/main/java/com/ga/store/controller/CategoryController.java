package com.ga.store.controller;

import com.ga.store.dto.CategoryRequest;
import com.ga.store.dto.CategoryResponse;
import com.ga.store.model.Category;
import com.ga.store.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(
        name = "Categories",
        description = "Product category browsing and admin category management"
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create category",
            description = "Creates a new product category. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category information"
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
                    responseCode = "409",
                    description = "Category already exists"
            )
    })
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        Category category =
                categoryService.createCategory(
                        request
                );

        CategoryResponse response =
                new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.isActive()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @SecurityRequirements
    @Operation(
            summary = "Get all categories",
            description = "Returns all available product categories."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories returned successfully"
    )
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> response =
                categoryService.getAllCategories()
                        .stream()
                        .map(category ->
                                new CategoryResponse(
                                        category.getId(),
                                        category.getName(),
                                        category.getDescription(),
                                        category.isActive()
                                ))
                        .toList();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(
            summary = "Get category by ID",
            description = "Returns a specific product category."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category returned successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(
                    description = "Category ID",
                    example = "1"
            )
            @PathVariable Long id) {

        Category category =
                categoryService.getCategoryById(
                        id
                );

        CategoryResponse response =
                new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.isActive()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update category",
            description = "Updates an existing category. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category information"
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
                    description = "Category name already exists"
            )
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(
                    description = "Category ID",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        Category category =
                categoryService.updateCategory(
                        id,
                        request
                );

        CategoryResponse response =
                new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.isActive()
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
            summary = "Delete category",
            description = "Deletes a category if it is not being used by products. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted successfully"
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
                    description = "Category cannot be deleted because it contains products"
            )
    })
    public ResponseEntity<String> deleteCategory(
            @Parameter(
                    description = "Category ID",
                    example = "1"
            )
            @PathVariable Long id) {

        categoryService.deleteCategory(
                id
        );

        return new ResponseEntity<>(
                "Category deleted successfully",
                HttpStatus.OK
        );
    }
}