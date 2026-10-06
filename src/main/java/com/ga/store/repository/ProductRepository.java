package com.ga.store.repository;

import com.ga.store.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    List<Product> findByCategoryId(
            Long categoryId
    );

    boolean existsByCategoryId(
            Long categoryId
    );

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(
            Long categoryId
    );

    Optional<Product> findByIdAndActiveTrue(
            Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            "SELECT p FROM Product p WHERE p.id = :id"
    )
    Optional<Product> findByIdForUpdate(
            @Param("id") Long id
    );
}