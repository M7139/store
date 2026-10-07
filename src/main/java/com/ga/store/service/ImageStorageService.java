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

@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_PIXELS = 16_000_000;

    private final Path productUploadDirectory =
            Paths.get("uploads/products").toAbsolutePath();

    private final Path profileUploadDirectory =
            Paths.get("uploads/profiles").toAbsolutePath();

    public ImageStorageService() {

        try {
            Files.createDirectories(productUploadDirectory);
            Files.createDirectories(profileUploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create image upload directory",
                    exception
            );
        }
    }

    public String saveImage(MultipartFile file) {

        return saveImage(
                file,
                productUploadDirectory,
                "/uploads/products/"
        );
    }

    public String saveProfileImage(MultipartFile file) {

        return saveImage(
                file,
                profileUploadDirectory,
                "/uploads/profiles/"
        );
    }

    private String saveImage(
            MultipartFile file,
            Path uploadDirectory,
            String imageUrlPath) {

        if (file.isEmpty()) {
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

                reader.setInput(imageInput, true, true);

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

            } finally {
                reader.dispose();
            }

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Could not read image file",
                    exception
            );
        }

        // Save decoded pixels instead of trusting the uploaded filename.
        String fileName = UUID.randomUUID() + ".png";

        Path filePath = uploadDirectory.resolve(fileName);

        try {

            if (!ImageIO.write(image, "png", filePath.toFile())) {
                throw new IOException(
                        "PNG encoder is unavailable"
                );
            }

        } catch (IOException exception) {

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw new IllegalStateException(
                    "Could not save image file",
                    exception
            );
        }

        return imageUrlPath + fileName;
    }

    public void deleteImage(String imageUrl) {

        deleteImage(
                imageUrl,
                productUploadDirectory
        );
    }

    public void deleteProfileImage(String imageUrl) {

        deleteImage(
                imageUrl,
                profileUploadDirectory
        );
    }

    private void deleteImage(
            String imageUrl,
            Path uploadDirectory) {

        String fileName = Paths.get(imageUrl)
                .getFileName()
                .toString();

        Path filePath = uploadDirectory
                .resolve(fileName)
                .normalize();

        if (!filePath.getParent().equals(uploadDirectory)) {
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