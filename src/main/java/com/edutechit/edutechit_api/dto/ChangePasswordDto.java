package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotBlankExceptSpaces(message = "Vui lòng nhập mật khẩu cũ")
    private String oldPassword;
    @NotBlankExceptSpaces(message = "Vui lòng nhập mật khẩu mới")
    private String newPassword;
    @NotBlankExceptSpaces(message = "Vui lòng nhập lại mật khẩu mới")
    private String reNewPassword;
}
