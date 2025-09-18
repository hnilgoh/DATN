package com.duantn.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VideoUrlValidator implements ConstraintValidator<ValidVideoUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // để @NotBlank kiểm tra nếu cần
        }

        String url = value.trim().toLowerCase();

        // Link nhúng YouTube
        if (url.matches("^https:\\/\\/www\\.youtube\\.com\\/embed\\/[\\w-]+(\\?.*)?$")) {
            return true;
        }

        // File video hợp lệ
        String[] extensions = { "mp4", "webm", "ogv", "mov", "mkv", "avi", "flv", "wmv", "m4v" };
        for (String ext : extensions) {
            if (url.matches("^https?:\\/\\/.+\\." + ext + "(\\?.*)?$")) {
                return true;
            }
        }

        return false;
    }
}
