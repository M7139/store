package com.ga.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path uploadDirectory =
            Paths.get("uploads/products");

    public ImageStorageService() {

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create image upload directory"
            );
        }
    }

    public String saveImage(MultipartFile file) {

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

        String originalFilename = file.getOriginalFilename();

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

        return "/uploads/products/" + fileName;
    }
}