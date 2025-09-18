package com.duantn.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.duantn.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String uploadImage(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString(); // dùng secure_url cho HTTPS
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại!", e);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Xoá ảnh khỏi Cloudinary thất bại!", e);
        }
    }

    // @Override
    // public String extractPublicIdFromUrl(String imageUrl) {
    // if (imageUrl == null || !imageUrl.contains("/"))
    // return null;

    // String[] parts = imageUrl.split("/");
    // String fileNameWithExtension = parts[parts.length - 1];

    // // Loại bỏ đuôi mở rộng
    // String fileName = fileNameWithExtension.contains(".")
    // ? fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'))
    // : fileNameWithExtension;

    // // Lấy thư mục chứa file
    // String folder = parts.length >= 2 ? parts[parts.length - 2] : "";

    // return folder + "/" + fileName;
    // }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())
            return null;

        try {
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1)
                return null;

            String path = imageUrl.substring(uploadIndex + "/upload/".length());

            // Bỏ version nếu có (v1234567890/)
            if (path.startsWith("v")) {
                int slashAfterVersion = path.indexOf("/", 1);
                if (slashAfterVersion != -1) {
                    path = path.substring(slashAfterVersion + 1);
                }
            }

            // Bỏ đuôi mở rộng
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex != -1) {
                path = path.substring(0, dotIndex);
            }

            return path;
        } catch (Exception e) {
            return null;
        }
    }

}