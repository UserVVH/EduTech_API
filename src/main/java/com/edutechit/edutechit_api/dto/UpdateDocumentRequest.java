package com.edutechit.edutechit_api.dto;

import com.edutechit.edutechit_api.validation.FileType;
import com.edutechit.edutechit_api.validation.NotBlankExceptSpaces;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class UpdateDocumentRequest {
    @NotBlankExceptSpaces(message = "Vui lòng nhập tiêu đề cho tài liệu")
    private String title;
    @NotBlankExceptSpaces(message = "Vui lòng nhập mô tả cho tài liệu")
    private String description;
    @NotBlankExceptSpaces(message = "Vui lòng chọn loại tài liệu")
    private Set<Long> categoryId;

    @NotNull(message = "Vui lòng chọn ảnh đại diện")
    @FileType(allowedTypes = {"image/jpeg", "image/png"}, message = "Vui lòng chọn ảnh có định dạng hợp lệ (JPEG, PNG)")
    private MultipartFile image;

    @NotNull(message = "Vui lòng chọn ít nhất một tệp PDF")
    @NotEmpty(message = "Danh sách tệp PDF không được để trống")
    @FileType(allowedTypes = {"application/pdf"}, message = "Vui lòng chọn tệp PDF hợp lệ")
    private Set<MultipartFile> pdfFiles;

    private String author;
    private String publisher;
    private String publishingYear;
}

