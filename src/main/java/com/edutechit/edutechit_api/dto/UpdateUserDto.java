package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.FileType;
import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class UpdateUserDto {

    private String fullname;

//    @NotNull(message = "Vui lòng chọn ảnh đại diện")
//    @FileType(allowedTypes = {"image/jpeg", "image/png"}, message = "Vui lòng chọn ảnh có định dạng hợp lệ (JPEG, PNG)")
    private MultipartFile avatar;
    private String identifier;
    private String address;
}
