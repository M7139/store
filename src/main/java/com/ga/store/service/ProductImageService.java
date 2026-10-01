package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.model.ProductImage;
import com.ga.store.repository.ProductImageRepository;
import com.ga.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductRepository productRepository,
            ImageStorageService imageStorageService) {

        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<ProductImage> getImagesByProductId(Long productId) {

        getProductById(productId);

        return productImageRepository.findByProductId(productId);
    }

    private Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id " + id + " not found"
                        ));
    }

    public ProductImage getProductImageById(Long id) {

        return productImageRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product image with id " + id + " not found"
                        ));
    }

    public ProductImage uploadProductImage(
            MultipartFile file,
            Long productId,
            boolean primaryImage) {

        Product product =
                getProductById(productId);

        String imageUrl =
                imageStorageService.saveImage(file);

        List<ProductImage> existingImages =
                productImageRepository.findByProductId(productId);

        if (existingImages.isEmpty()) {
            primaryImage = true;
        }

        if (primaryImage) {

            productImageRepository
                    .findByProductIdAndPrimaryImageTrue(productId)
                    .ifPresent(existingPrimaryImage -> {
                        existingPrimaryImage.setPrimaryImage(false);
                        productImageRepository.save(existingPrimaryImage);
                    });
        }

        ProductImage productImage = new ProductImage(
                imageUrl,
                primaryImage,
                product
        );

        return productImageRepository.save(productImage);
    }

    public ProductImage setPrimaryImage(Long id) {

        ProductImage productImage =
                getProductImageById(id);

        Long productId =
                productImage.getProduct().getId();

        productImageRepository
                .findByProductIdAndPrimaryImageTrue(productId)
                .ifPresent(existingPrimaryImage -> {
                    existingPrimaryImage.setPrimaryImage(false);
                    productImageRepository.save(existingPrimaryImage);
                });

        productImage.setPrimaryImage(true);

        return productImageRepository.save(productImage);
    }

    public void deleteProductImage(Long id) {

        ProductImage productImage =
                getProductImageById(id);

        Long productId =
                productImage.getProduct().getId();

        boolean wasPrimary =
                productImage.isPrimaryImage();

        imageStorageService.deleteImage(
                productImage.getImageUrl()
        );

        productImageRepository.delete(productImage);

        if (wasPrimary) {

            List<ProductImage> remainingImages =
                    productImageRepository.findByProductId(productId);

            if (!remainingImages.isEmpty()) {

                ProductImage newPrimaryImage =
                        remainingImages.get(0);

                newPrimaryImage.setPrimaryImage(true);

                productImageRepository.save(newPrimaryImage);
            }
        }
    }

    public void deleteImagesByProductId(Long productId) {

        List<ProductImage> productImages =
                productImageRepository.findByProductId(productId);

        for (ProductImage productImage : productImages) {

            imageStorageService.deleteImage(
                    productImage.getImageUrl()
            );

            productImageRepository.delete(productImage);
        }
    }
}