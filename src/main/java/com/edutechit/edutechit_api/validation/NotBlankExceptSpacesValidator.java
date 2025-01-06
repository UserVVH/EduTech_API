package com.edutechit.edutechit_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotBlankExceptSpacesValidator implements
    ConstraintValidator<NotBlankExceptSpaces, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return StringUtils.hasText(
        value); // Kiểm tra không null, không rỗng và không chỉ chứa khoảng trắng
  }
}
