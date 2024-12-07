package com.edutechit.edutechit_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
// Kiểm tra loại tệp cho Set<MultipartFile>
public class FileTypeForCollectionValidator implements ConstraintValidator<FileType, Set<MultipartFile>> {
    private String[] allowedTypes; // Danh sách các định dạng tệp cho phép

    // Khởi tạo
    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowedTypes(); // Lấy danh sách định dạng tệp cho phép từ annotation
    }

    // Phương thức kiểm tra tính hợp lệ của Set<MultipartFile>
    @Override
    public boolean isValid(Set<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true; // Nếu không có tệp nào, coi như hợp lệ
        }

        // Lặp qua tất cả các tệp trong Set
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty() && !Arrays.asList(allowedTypes).contains(file.getContentType())) {
                return false; // Nếu có một tệp không hợp lệ, trả về false
            }
        }

        return true;
    }
}

