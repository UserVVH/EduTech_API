package com.edutechit.edutechit_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {

    private final List<String> validRoles = Arrays.asList("USER", "STUDENT", "TEACHER");

    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        return role != null && validRoles.contains(role.toUpperCase());
    }
}
