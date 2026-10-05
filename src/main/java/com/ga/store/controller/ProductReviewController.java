package com.ga.store.controller;

import com.ga.store.dto.ProductReviewRequest;
import com.ga.store.dto.ProductReviewResponse;
import com.ga.store.service.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(
            ProductReviewService productReviewService) {

        this.productReviewService = productReviewService;
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductReviewResponse> createReview(
            Authentication authentication,
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
    public ResponseEntity<List<ProductReviewResponse>>
    getReviewsByProduct(
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
    public ResponseEntity<ProductReviewResponse> updateReview(
            Authentication authentication,
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
    public ResponseEntity<String> deleteReview(
            Authentication authentication,
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