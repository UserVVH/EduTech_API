package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import com.edutechit.edutechit_api.validation.PasswordMatches;
import com.edutechit.edutechit_api.validation.ValidIdentifier;
import com.edutechit.edutechit_api.validation.ValidRole;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
@PasswordMatches(message = "Mật khẩu và xác nhận mật khẩu không trùng khớp") // Đảm bảo mật khẩu trùng với mật khẩu nhập lại
@ValidIdentifier(message = "Identifier không hợp lệ cho vai trò được chọn") // Kiểm tra `identifier` dựa trên `role`
public class RegisterRequestDto {

    @NotBlankExceptSpaces(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ") // Kiểm tra định dạng email
    private String email;

    @NotBlankExceptSpaces(message = "Vui lòng nhập mật khẩu")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#.,()-_=+])[A-Za-z\\d@$!%*?&#]{8,}$",
            message = "Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm một chữ hoa, một chữ thường, một số và một ký tự đặc biệt")
    private String password;


    @NotBlankExceptSpaces(message = "Vui lòng nhập lại mật khẩu")
    private String repassword;

    @NotBlankExceptSpaces(message = "Vui lòng nhập họ và tên")
    private String fullname;

    private MultipartFile avatar;

    @ValidRole(message = "Vai trò không hợp lệ, chỉ có thể là USER, STUDENT hoặc TEACHER") // Kiểm tra vai trò hợp lệ
    private String role = "USER"; // Mặc định là USER

    private String identifier;

    private String address;

    // Constructor mặc định
    public RegisterRequestDto() {
        if (this.role == null || this.role.isEmpty()) {
            this.role = "USER"; // Nếu không có vai trò, mặc định là USER
        }
    }
}
