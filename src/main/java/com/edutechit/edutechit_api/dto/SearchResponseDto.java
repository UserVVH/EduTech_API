package com.edutechit.edutechit_api.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchResponseDto {
    private List<UserInfoDto> users;
    private List<DocumentResponseDto> documentsByTitle;
    private List<CategoryDto> categoryDtos;
}