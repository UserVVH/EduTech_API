package com.edutechit.edutechit_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = { FileTypeValidator.class, FileTypeForCollectionValidator.class }) // Thêm validator cho Set
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Áp dụng cho field và parameter
@Retention(RetentionPolicy.RUNTIME) // Thời gian chạy
public @interface FileType { // Tạo annotation mới
    String message() default "Định dạng tệp không hợp lệ"; // Message mặc định
    String[] allowedTypes() default {}; // Danh sách các định dạng tệp cho phép
    Class<?>[] groups() default {}; // Group mặc định
    Class<? extends Payload>[] payload() default {}; // Payload mặc định
}
