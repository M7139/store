package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.model.ProductImage;
import com.ga.store.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    public ProductImageService(
            ProductImageRepository productImageRepository,
            ProductService productService,
            ImageStorageService imageStorageService) {

        this.productImageRepository = productImageRepository;
        this.productService = productService;
        this.imageStorageService = imageStorageService;
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

    public ProductImage uploadProductImage(
            MultipartFile file,
            Long productId,
            boolean primaryImage) {

        Product product =
                productService.getProductById(productId);

        String imageUrl =
                imageStorageService.saveImage(file);

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
}