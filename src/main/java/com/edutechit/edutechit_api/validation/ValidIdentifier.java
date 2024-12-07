package com.edutechit.edutechit_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidIdentifierValidator.class)
@Target({ ElementType.TYPE }) // Áp dụng cho cấp độ lớp
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIdentifier {
    String message() default "Identifier không hợp lệ cho vai trò được chọn";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
