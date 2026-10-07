package com.ga.store.controller;

import com.ga.store.dto.ProductReviewRequest;
import com.ga.store.dto.ProductReviewResponse;
import com.ga.store.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(
        name = "Product Reviews",
        description = "Customer product reviews and ratings"
)
@SecurityRequirement(name = "bearerAuth")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(
            ProductReviewService productReviewService) {

        this.productReviewService = productReviewService;
    }

    @PostMapping("/product/{productId}")
    @Operation(
            summary = "Create product review",
            description = "Creates a review for a product. The customer must have received the product in a delivered order and may only review each product once."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid review information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Customer is not eligible to review the product or review already exists"
            )
    })
    public ResponseEntity<ProductReviewResponse> createReview(
            Authentication authentication,

            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @PathVariable Long productId,

            @Valid @RequestBody ProductReviewRequest request) {

        String email =
                authentication.getName();

        ProductReviewResponse response =
                productReviewService.createReview(
                        email,
                        productId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/product/{productId}")
    @Operation(
            summary = "Get product reviews",
            description = "Returns reviews for a specific product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviews returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<List<ProductReviewResponse>>
    getReviewsByProduct(
            @Parameter(
                    description = "Product ID",
                    example = "1"
            )
            @PathVariable Long productId) {

        List<ProductReviewResponse> response =
                productReviewService
                        .getReviewsByProduct(
                                productId
                        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get my reviews",
            description = "Returns all reviews created by the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviews returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<List<ProductReviewResponse>>
    getMyReviews(
            Authentication authentication) {

        String email =
                authentication.getName();

        List<ProductReviewResponse> response =
                productReviewService
                        .getReviewsByUser(
                                email
                        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/{reviewId}")
    @Operation(
            summary = "Update review",
            description = "Updates a review belonging to the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid review information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    public ResponseEntity<ProductReviewResponse> updateReview(
            Authentication authentication,

            @Parameter(
                    description = "Review ID",
                    example = "1"
            )
            @PathVariable Long reviewId,

            @Valid @RequestBody ProductReviewRequest request) {

        String email =
                authentication.getName();

        ProductReviewResponse response =
                productReviewService.updateReview(
                        email,
                        reviewId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{reviewId}")
    @Operation(
            summary = "Delete review",
            description = "Deletes a review belonging to the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    public ResponseEntity<String> deleteReview(
            Authentication authentication,

            @Parameter(
                    description = "Review ID",
                    example = "1"
            )
            @PathVariable Long reviewId) {

        String email =
                authentication.getName();

        productReviewService.deleteReview(
                email,
                reviewId
        );

        return new ResponseEntity<>(
                "Review deleted successfully",
                HttpStatus.OK
        );
    }
}