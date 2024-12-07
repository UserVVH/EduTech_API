package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDocumentStatsDTO {
    private Long userId;
    private String fullname;
    private String email;
    private String identifier;
    private String roleName;
    private String avatar;
    private Long documentCount;
    private Long totalViews;
}

