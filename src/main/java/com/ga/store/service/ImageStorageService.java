package com.ga.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Handles local storage and deletion of product and profile images.
 */
@Service
public class ImageStorageService {

    private final Path productUploadDirectory =
            Paths.get("uploads/products");

    private final Path profileUploadDirectory =
            Paths.get("uploads/profiles");

    public ImageStorageService() {

        try {
            Files.createDirectories(productUploadDirectory);
            Files.createDirectories(profileUploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create image upload directory"
            );
        }
    }

    /**
     * Stores a product image.
     *
     * @param file image file
     * @return public image URL
     */
    public String saveImage(MultipartFile file) {

        return saveImage(
                file,
                productUploadDirectory,
                "/uploads/products/"
        );
    }

    /**
     * Stores a user profile image.
     *
     * @param file image file
     * @return public image URL
     */
    public String saveProfileImage(MultipartFile file) {

        return saveImage(
                file,
                profileUploadDirectory,
                "/uploads/profiles/"
        );
    }

    /**
     * Validates and saves an image using a unique filename.
     *
     * @param file image file
     * @param uploadDirectory storage directory
     * @param imageUrlPath public URL prefix
     * @return public image URL
     */
    private String saveImage(
            MultipartFile file,
            Path uploadDirectory,
            String imageUrlPath) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Image file cannot be empty"
            );
        }

        if (file.getContentType() == null
                || !file.getContentType().startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null
                && originalFilename.contains(".")) {

            extension = originalFilename.substring(
                    originalFilename.lastIndexOf(".")
            );
        }

        String fileName =
                UUID.randomUUID() + extension;

        Path filePath =
                uploadDirectory.resolve(fileName);

        try {
            file.transferTo(filePath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save image file"
            );
        }

        return imageUrlPath + fileName;
    }

    /**
     * Deletes a product image.
     *
     * @param imageUrl stored image URL
     */
    public void deleteImage(String imageUrl) {

        deleteImage(
                imageUrl,
                productUploadDirectory
        );
    }

    /**
     * Deletes a user profile image.
     *
     * @param imageUrl stored image URL
     */
    public void deleteProfileImage(String imageUrl) {

        deleteImage(
                imageUrl,
                profileUploadDirectory
        );
    }

    /**
     * Deletes a stored image from the supplied directory.
     *
     * @param imageUrl image URL
     * @param uploadDirectory storage directory
     */
    private void deleteImage(
            String imageUrl,
            Path uploadDirectory) {

        String fileName =
                Paths.get(imageUrl).getFileName().toString();

        Path filePath =
                uploadDirectory.resolve(fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not delete image file"
            );
        }
    }
}