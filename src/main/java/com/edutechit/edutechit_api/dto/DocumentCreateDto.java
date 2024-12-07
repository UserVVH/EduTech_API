package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.FileType;
import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentCreateDto {

    @NotBlankExceptSpaces(message = "Vui lòng nhập tiêu đề cho tài liệu")
    private String title;

    @NotBlankExceptSpaces(message = "Vui lòng nhập mô tả cho tài liệu")
    private String description;

    @NotNull(message = "Vui lòng chọn danh mục cho tài liệu")
    private Long categoryId;

    @NotNull(message = "Vui lòng tải lên hình ảnh cho tài liệu")
    @FileType(allowedTypes = {"image/png", "image/jpeg", "image/jpg"}, message = "Hình ảnh phải có định dạng PNG, JPEG hoặc JPG")
    private MultipartFile image;

    @NotNull(message = "Vui lòng tải lên một file PDF")
    @FileType(allowedTypes = {"application/pdf"}, message = "File phải có định dạng PDF")
    private MultipartFile pdfFiles;

    private String author;
    private String publisher;
    private String publishingYear;
}