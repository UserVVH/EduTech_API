package com.edutechit.edutechit_api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageFile {
    String message() default "Invalid image file. Allowed extensions are .png, .jpg, .jpeg, .pdf";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}