package com.edutechit.edutechit_api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PdfFileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPdfFile {
    String message() default "Invalid PDF file. Allowed extension is .pdf";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}