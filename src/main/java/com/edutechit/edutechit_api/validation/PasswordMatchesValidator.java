package com.edutechit.edutechit_api.validation;

import com.edutechit.edutechit_api.dto.RegisterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        RegisterRequestDto user = (RegisterRequestDto) obj;
        return user.getPassword().equals(user.getRepassword());
    }
}

