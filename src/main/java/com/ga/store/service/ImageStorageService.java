package com.ga.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

/**
 * Handles local storage and deletion of product and profile images.
 * Uploaded files are validated by their actual image contents
 * and re-encoded as PNG files before storage.
 */
@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE =
            5L * 1024 * 1024;

    private static final long MAX_PIXELS =
            16_000_000L;

    private final Path productUploadDirectory =
            Paths.get("uploads/products").toAbsolutePath();

    private final Path profileUploadDirectory =
            Paths.get("uploads/profiles").toAbsolutePath();

    /**
     * Creates the directories used to store uploaded images.
     *
     * @throws IllegalStateException if the directories cannot be created
     */
    public ImageStorageService() {

        try {

            Files.createDirectories(
                    productUploadDirectory
            );

            Files.createDirectories(
                    profileUploadDirectory
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not create image upload directory",
                    exception
            );
        }
    }

    /**
     * Validates and stores a product image.
     *
     * @param file PNG or JPEG image file
     * @return public URL of the stored PNG image
     */
    public String saveImage(MultipartFile file) {

        return saveImage(
                file,
                productUploadDirectory,
                "/uploads/products/"
        );
    }

    /**
     * Validates and stores a user profile image.
     *
     * @param file PNG or JPEG image file
     * @return public URL of the stored PNG image
     */
    public String saveProfileImage(MultipartFile file) {

        return saveImage(
                file,
                profileUploadDirectory,
                "/uploads/profiles/"
        );
    }

    /**
     * Validates an uploaded image and saves it using a unique filename.
     * Validation checks the file size, actual image format and pixel count.
     * The uploaded filename and declared content type are not trusted.
     * Images are re-encoded as PNG to avoid retaining the original file contents.
     *
     * @param file image file
     * @param uploadDirectory storage directory
     * @param imageUrlPath public URL prefix
     * @return public URL of the stored PNG image
     * @throws IllegalArgumentException if the image is empty, invalid,
     *                                  unsupported or exceeds the limits
     * @throws IllegalStateException if the image cannot be saved
     */
    private String saveImage(
            MultipartFile file,
            Path uploadDirectory,
            String imageUrlPath) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "Image must be 5 MB or smaller"
            );
        }

        BufferedImage image;

        try (
                var input = file.getInputStream();
                ImageInputStream imageInput =
                        ImageIO.createImageInputStream(input)
        ) {

            if (imageInput == null) {

                throw new IllegalArgumentException(
                        "Invalid image file"
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(imageInput);

            if (!readers.hasNext()) {

                throw new IllegalArgumentException(
                        "Only PNG and JPEG images are allowed"
                );
            }

            ImageReader reader = readers.next();

            try {

                String format = reader.getFormatName()
                        .toLowerCase(Locale.ROOT);

                if (!format.equals("png")
                        && !format.equals("jpeg")
                        && !format.equals("jpg")) {

                    throw new IllegalArgumentException(
                            "Only PNG and JPEG images are allowed"
                    );
                }

                reader.setInput(
                        imageInput,
                        true,
                        true
                );

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0
                        || height <= 0
                        || (long) width * height > MAX_PIXELS) {

                    throw new IllegalArgumentException(
                            "Image dimensions are too large"
                    );
                }

                image = reader.read(0);

                if (image == null) {

                    throw new IllegalArgumentException(
                            "Invalid image file"
                    );
                }

            } finally {

                reader.dispose();
            }

        } catch (IOException exception) {

            throw new IllegalArgumentException(
                    "Could not read image file",
                    exception
            );
        }

        String fileName =
                UUID.randomUUID() + ".png";

        Path filePath =
                uploadDirectory.resolve(fileName);

        try {

            boolean saved = ImageIO.write(
                    image,
                    "png",
                    filePath.toFile()
            );

            if (!saved) {

                throw new IOException(
                        "PNG encoder is unavailable"
                );
            }

        } catch (IOException exception) {

            try {

                Files.deleteIfExists(filePath);

            } catch (IOException cleanupException) {

                exception.addSuppressed(
                        cleanupException
                );
            }

            throw new IllegalStateException(
                    "Could not save image file",
                    exception
            );
        }

        return imageUrlPath + fileName;
    }

    /**
     * Deletes a stored product image.
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
     * Deletes a stored user profile image.
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
     * Only the filename from the URL is used, and the resolved
     * path must remain directly inside the storage directory.
     *
     * @param imageUrl stored image URL
     * @param uploadDirectory storage directory
     * @throws IllegalArgumentException if the image URL or path is invalid
     * @throws IllegalStateException if the image cannot be deleted
     */
    private void deleteImage(
            String imageUrl,
            Path uploadDirectory) {

        if (imageUrl == null || imageUrl.isBlank()) {

            throw new IllegalArgumentException(
                    "Image URL cannot be empty"
            );
        }

        Path imagePath = Paths.get(imageUrl);

        if (imagePath.getFileName() == null) {

            throw new IllegalArgumentException(
                    "Invalid image path"
            );
        }

        String fileName =
                imagePath.getFileName().toString();

        Path filePath = uploadDirectory
                .resolve(fileName)
                .normalize();

        if (!uploadDirectory.equals(filePath.getParent())) {

            throw new IllegalArgumentException(
                    "Invalid image path"
            );
        }

        try {

            Files.deleteIfExists(filePath);

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not delete image file",
                    exception
            );
        }
    }
}