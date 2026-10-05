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