package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import lombok.*;

import javax.validation.constraints.Email;
import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginDto implements Serializable {
    @NotBlankExceptSpaces(message = "Vui lòng nhập email")
    @Email(message = "Email không hợp lệ")
    public String email;
    @NotBlankExceptSpaces(message = "Vui lòng nhập mật khẩu")
    public String password;
}
