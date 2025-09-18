package com.duantn.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;

    void deleteImage(String publicId);

    String extractPublicIdFromUrl(String imageUrl);
}