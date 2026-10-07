package com.ga.store.service;

import com.ga.store.dto.ProductRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.model.Category;
import com.ga.store.model.Product;
import com.ga.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductImageService productImageService;

    private ProductService productService;

    @BeforeEach
    void setUp() {

        productService = new ProductService(
                productRepository,
                categoryService,
                productImageService
        );
    }

    @Test
    void createProductShouldCreateProductWhenNameIsAvailable() {

        ProductRequest request =
                new ProductRequest(
                        "Lavender Soap",
                        "Lavender scented soap",
                        new BigDecimal("2.50"),
                        10,
                        1L
                );

        Category category =
                new Category(
                        "Bar Soap",
                        "Soap bars"
                );

        category.setId(1L);

        when(productRepository.existsByNameIgnoreCase(
                "Lavender Soap"
        )).thenReturn(false);

        when(categoryService.getCategoryById(
                1L
        )).thenReturn(category);

        when(productRepository.save(
                org.mockito.ArgumentMatchers.any(Product.class)
        )).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Product result =
                productService.createProduct(
                        request
                );

        assertEquals(
                "Lavender Soap",
                result.getName()
        );

        assertEquals(
                new BigDecimal("2.50"),
                result.getPrice()
        );

        assertEquals(
                10,
                result.getStockQuantity()
        );

        assertEquals(
                category,
                result.getCategory()
        );
    }

    @Test
    void createProductShouldFailWhenNameAlreadyExists() {

        ProductRequest request =
                new ProductRequest(
                        "Lavender Soap",
                        "Lavender scented soap",
                        new BigDecimal("2.50"),
                        10,
                        1L
                );

        when(productRepository.existsByNameIgnoreCase(
                "Lavender Soap"
        )).thenReturn(true);

        assertThrows(
                InformationExistsException.class,
                () -> productService.createProduct(
                        request
                )
        );
    }

    @Test
    void searchActiveProductsShouldFailWhenPageIsNegative() {

        assertThrows(
                InformationExistsException.class,
                () -> productService.searchActiveProducts(
                        null,
                        null,
                        -1,
                        10,
                        "name,asc"
                )
        );
    }

    @Test
    void searchActiveProductsShouldFailWhenSortFieldIsInvalid() {

        assertThrows(
                InformationExistsException.class,
                () -> productService.searchActiveProducts(
                        null,
                        null,
                        0,
                        10,
                        "stockQuantity,asc"
                )
        );
    }
}