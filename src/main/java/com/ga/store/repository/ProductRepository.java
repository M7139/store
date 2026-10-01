package com.ga.store.repository;

import com.ga.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    Optional<Product> findByIdAndActiveTrue(Long id);
}