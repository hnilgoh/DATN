package com.duantn.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VideoUrlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVideoUrl {
    String message() default "Đường dẫn video không hợp lệ (chỉ hỗ trợ link nhúng YouTube hoặc file video hợp lệ)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}