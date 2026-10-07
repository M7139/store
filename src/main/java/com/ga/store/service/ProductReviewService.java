package com.ga.store.service;

import com.ga.store.dto.ProductReviewRequest;
import com.ga.store.dto.ProductReviewResponse;
import com.ga.store.enums.OrderStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.model.ProductReview;
import com.ga.store.model.User;
import com.ga.store.repository.OrderItemRepository;
import com.ga.store.repository.ProductReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles customer product reviews and review eligibility rules.
 */
@Service
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public ProductReviewService(
            ProductReviewRepository productReviewRepository,
            OrderItemRepository orderItemRepository,
            UserService userService,
            ProductService productService) {

        this.productReviewRepository = productReviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
        this.productService = productService;
    }

    /**
     * Creates a product review if the customer has received
     * the product in a delivered order and has not reviewed it before.
     *
     * @param email authenticated customer's email
     * @param productId product ID
     * @param request review information
     * @return created review
     */
    public ProductReviewResponse createReview(
            String email,
            Long productId,
            ProductReviewRequest request) {

        User user =
                userService.getUserByEmail(email);

        Product product =
                productService.getActiveProductById(
                        productId
                );

        boolean purchasedAndDelivered =
                orderItemRepository
                        .existsByOrderUserAndProductAndOrderStatus(
                                user,
                                product,
                                OrderStatus.DELIVERED
                        );

        if (!purchasedAndDelivered) {

            throw new InformationExistsException(
                    "You can only review products from delivered orders"
            );
        }

        if (productReviewRepository
                .existsByUserAndProduct(
                        user,
                        product
                )) {

            throw new InformationExistsException(
                    "You have already reviewed this product"
            );
        }

        String comment = request.getComment();

        if (comment != null) {
            comment = comment.trim();
        }

        ProductReview productReview =
                new ProductReview(
                        user,
                        product,
                        request.getRating(),
                        comment
                );

        ProductReview savedReview =
                productReviewRepository.save(
                        productReview
                );

        return createProductReviewResponse(
                savedReview
        );
    }

    /**
     * Returns all reviews for a product.
     *
     * @param productId product ID
     * @return product reviews
     */
    public List<ProductReviewResponse> getReviewsByProduct(
            Long productId) {

        Product product =
                productService.getActiveProductById(
                        productId
                );

        return productReviewRepository
                .findByProductOrderByCreatedAtDesc(
                        product
                )
                .stream()
                .map(this::createProductReviewResponse)
                .toList();
    }

    /**
     * Returns all reviews written by a customer.
     *
     * @param email authenticated customer's email
     * @return customer reviews
     */
    public List<ProductReviewResponse> getReviewsByUser(
            String email) {

        User user =
                userService.getUserByEmail(email);

        return productReviewRepository
                .findByUserOrderByCreatedAtDesc(
                        user
                )
                .stream()
                .map(this::createProductReviewResponse)
                .toList();
    }

    /**
     * Updates a review if it belongs to the authenticated customer.
     *
     * @param email authenticated customer's email
     * @param reviewId review ID
     * @param request updated review information
     * @return updated review
     */
    public ProductReviewResponse updateReview(
            String email,
            Long reviewId,
            ProductReviewRequest request) {

        User user =
                userService.getUserByEmail(email);

        ProductReview productReview =
                productReviewRepository
                        .findById(reviewId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Review not found"
                                ));

        if (!productReview.getUser()
                .getId()
                .equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Review not found"
            );
        }

        String comment = request.getComment();

        if (comment != null) {
            comment = comment.trim();
        }

        productReview.setRating(
                request.getRating()
        );

        productReview.setComment(
                comment
        );

        ProductReview savedReview =
                productReviewRepository.save(
                        productReview
                );

        return createProductReviewResponse(
                savedReview
        );
    }

    /**
     * Deletes a review belonging to the authenticated customer.
     *
     * @param email authenticated customer's email
     * @param reviewId review ID
     */
    public void deleteReview(
            String email,
            Long reviewId) {

        User user =
                userService.getUserByEmail(email);

        ProductReview productReview =
                productReviewRepository
                        .findById(reviewId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Review not found"
                                ));

        if (!productReview.getUser()
                .getId()
                .equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Review not found"
            );
        }

        productReviewRepository.delete(
                productReview
        );
    }

    /**
     * Converts a review entity into an API response.
     *
     * @param productReview review entity
     * @return review response
     */
    private ProductReviewResponse createProductReviewResponse(
            ProductReview productReview) {

        User user =
                productReview.getUser();

        String userName =
                user.getFirstName()
                        + " "
                        + user.getLastName();

        return new ProductReviewResponse(
                productReview.getId(),
                productReview.getProduct().getId(),
                user.getId(),
                userName,
                productReview.getRating(),
                productReview.getComment(),
                productReview.getCreatedAt(),
                productReview.getUpdatedAt()
        );
    }
}