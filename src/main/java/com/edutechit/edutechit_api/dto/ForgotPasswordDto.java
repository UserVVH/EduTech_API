package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class ForgotPasswordDto {
    @NotBlankExceptSpaces(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    private String email;
}
