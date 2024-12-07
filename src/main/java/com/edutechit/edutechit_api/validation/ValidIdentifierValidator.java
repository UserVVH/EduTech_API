package com.edutechit.edutechit_api.validation;

import com.edutechit.edutechit_api.dto.RegisterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidIdentifierValidator implements ConstraintValidator<ValidIdentifier, RegisterRequestDto> {

    @Override
    public void initialize(ValidIdentifier constraintAnnotation) {
    }

    @Override
    public boolean isValid(RegisterRequestDto registerRequestDto, ConstraintValidatorContext context) {
        String role = registerRequestDto.getRole();
        String identifier = registerRequestDto.getIdentifier();

        // Kiểm tra nếu vai trò là USER, identifier phải rỗng
        if ("USER".equals(role)) {
            if (identifier == null || identifier.isEmpty()) {
                return true;
            } else {
                // Thêm thông báo lỗi tùy chỉnh cho role USER
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Vai trò USER không được phép có identifier.")
                        .addPropertyNode("identifier")
                        .addConstraintViolation();
                return false;
            }
        }

        // Kiểm tra nếu vai trò là STUDENT, identifier phải có định dạng SV + 6 số
        if ("STUDENT".equals(role)) {
            if (identifier != null && identifier.matches("^SV\\d{6}$")) {
                return true;
            } else {
                // Thêm thông báo lỗi tùy chỉnh cho role STUDENT
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Identifier cho vai trò STUDENT phải có định dạng SV + 6 số.")
                        .addPropertyNode("identifier")
                        .addConstraintViolation();
                return false;
            }
        }

        // Kiểm tra nếu vai trò là TEACHER, identifier phải có định dạng GV + 6 số
        if ("TEACHER".equals(role)) {
            if (identifier != null && identifier.matches("^GV\\d{6}$")) {
                return true;
            } else {
                // Thêm thông báo lỗi tùy chỉnh cho role TEACHER
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Identifier cho vai trò TEACHER phải có định dạng GV + 6 số.")
                        .addPropertyNode("identifier")
                        .addConstraintViolation();
                return false;
            }
        }

        // Nếu không hợp lệ, trả về false và thêm thông báo chung
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Vai trò không hợp lệ.")
                .addPropertyNode("role")
                .addConstraintViolation();
        return false;
    }
}
