package com.ga.store.service;

import com.ga.store.dto.ProductImageRequest;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.model.ProductImage;
import com.ga.store.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductService productService) {

        this.productImageRepository = productImageRepository;
        this.productService = productService;
    }

    public List<ProductImage> getImagesByProductId(Long productId) {

        productService.getProductById(productId);

        return productImageRepository.findByProductId(productId);
    }

    public ProductImage getProductImageById(Long id) {

        return productImageRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product image with id " + id + " not found"
                        ));
    }

    public ProductImage createProductImage(ProductImageRequest request) {

        Product product =
                productService.getProductById(request.getProductId());

        if (request.isPrimaryImage()) {

            productImageRepository
                    .findByProductIdAndPrimaryImageTrue(product.getId())
                    .ifPresent(existingPrimaryImage -> {
                        existingPrimaryImage.setPrimaryImage(false);
                        productImageRepository.save(existingPrimaryImage);
                    });
        }

        ProductImage productImage = new ProductImage(
                request.getImageUrl().trim(),
                request.isPrimaryImage(),
                product
        );

        return productImageRepository.save(productImage);
    }
}