package com.edutechit.edutechit_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotBlankExceptSpacesValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankExceptSpaces {
    String message() default "Trường này không được để trống";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
