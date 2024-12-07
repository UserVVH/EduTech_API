package com.edutechit.edutechit_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListUserDto {
    private Long id;
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

    // Constructor
    public ListUserDto(Long userid, String fullname, String email, String address, String identifier, String avatar,
                       LocalDateTime createdAt, LocalDateTime updatedAt, Boolean enabled, String role) {
        this.id = userid;
        this.fullname = fullname;
        this.email = email;
        this.address = address;
        this.identifier = identifier;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.enabled = enabled;
        this.role = role;
    }
}
