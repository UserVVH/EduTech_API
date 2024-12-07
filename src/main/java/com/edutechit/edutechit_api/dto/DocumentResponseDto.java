package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.util.TimeUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DocumentResponseDto {
    private Long id;
    private String title;
    private String userName;
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private String publisher;
    private String publishingYear;
    private String image;
    private String categoryName;
    private int view;
    private Document.Status status;

    public String getRelativeCreatedAt() {
        return TimeUtils.getRelativeTime(createdAt);
    }

    public String getRelativeUpdatedAt() {
        return TimeUtils.getRelativeTime(updatedAt);
    }
}
