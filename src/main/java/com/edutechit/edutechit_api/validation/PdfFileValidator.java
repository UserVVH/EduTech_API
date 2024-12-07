package com.edutechit.edutechit_api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PdfFileValidator implements ConstraintValidator<ValidPdfFile, MultipartFile> {

    @Override
    public void initialize(ValidPdfFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Allow null or empty files, use @NotNull or @NotBlank for required validation
        }
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.matches("([^\\s]+(\\.(?i)(pdf))$)");
    }
}