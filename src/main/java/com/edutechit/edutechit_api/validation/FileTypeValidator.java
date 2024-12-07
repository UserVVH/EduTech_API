package com.edutechit.edutechit_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {
    private String[] allowedTypes;

    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

//    @Override
//    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
//        if (file == null || file.isEmpty()) {
//            return true; // Nếu tệp không tồn tại hoặc rỗng, không cần kiểm tra
//        }
//
//        return Arrays.asList(allowedTypes).contains(file.getContentType());
//    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        return contentType != null && Arrays.asList(allowedTypes).contains(contentType);
    }

}

