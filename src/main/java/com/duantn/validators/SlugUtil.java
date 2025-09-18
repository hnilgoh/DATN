package com.duantn.validators;

import java.text.Normalizer;

public class SlugUtil {
    public static String toSlug(String input) {
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // bỏ dấu
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "") // bỏ ký tự đặc biệt
                .replaceAll("\\s+", "-"); // khoảng trắng → -
        return slug;
    }
}