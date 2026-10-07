package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Product;
import com.ga.store.model.ProductImage;
import com.ga.store.repository.ProductImageRepository;
import com.ga.store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Handles product image upload, retrieval, primary image
 * selection and deletion.
 */
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

    /**
     * Returns all images belonging to a product.
     *
     * @param productId product ID
     * @return product images
     */
    public List<ProductImage> getImagesByProductId(Long productId) {

        getProductById(productId);

        return productImageRepository.findByProductId(productId);
    }

    /**
     * Returns images only if the associated product is active.
     *
     * @param productId product ID
     * @return product images
     */
    public List<ProductImage> getActiveProductImagesByProductId(
            Long productId) {

        getActiveProductById(productId);

        return productImageRepository.findByProductId(productId);
    }

    /**
     * Finds a product by ID.
     *
     * @param id product ID
     * @return product
     */
    private Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id " + id + " not found"
                        ));
    }

    /**
     * Finds an active product by ID.
     *
     * @param id product ID
     * @return active product
     */
    private Product getActiveProductById(Long id) {

        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product with id " + id + " not found"
                        ));
    }

    /**
     * Finds a product image by ID.
     *
     * @param id image ID
     * @return product image
     */
    public ProductImage getProductImageById(Long id) {

        return productImageRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Product image with id " + id + " not found"
                        ));
    }

    /**
     * Returns the primary image URL for a product.
     *
     * @param productId product ID
     * @return image URL or null if no primary image exists
     */
    public String getPrimaryImageUrl(Long productId) {

        return productImageRepository
                .findByProductIdAndPrimaryImageTrue(productId)
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }

    /**
     * Uploads a new image for a product.
     * The first uploaded image automatically becomes primary.
     *
     * @param file image file
     * @param productId product ID
     * @param primaryImage whether the image should be primary
     * @return created product image
     */
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

    /**
     * Sets an image as the primary image for its product.
     *
     * @param id image ID
     * @return updated image
     */
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

    /**
     * Deletes a product image and selects another primary
     * image when necessary.
     *
     * @param id image ID
     */
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

    /**
     * Deletes every image belonging to a product.
     *
     * @param productId product ID
     */
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