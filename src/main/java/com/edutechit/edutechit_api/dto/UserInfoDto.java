package com.edutechit.edutechit_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInfoDto {
    private String fullname;
    private String email;
    private String address;
    private String identifier;
    private String avatar;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Boolean enabled;
    private String role;
}