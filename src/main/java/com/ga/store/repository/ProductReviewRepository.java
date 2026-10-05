package com.ga.store.repository;

import com.ga.store.model.Product;
import com.ga.store.model.ProductReview;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository
        extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductOrderByCreatedAtDesc(
            Product product
    );

    Optional<ProductReview> findByUserAndProduct(
            User user,
            Product product
    );

    boolean existsByUserAndProduct(
            User user,
            Product product
    );

    List<ProductReview> findByUserOrderByCreatedAtDesc(
            User user
    );
}