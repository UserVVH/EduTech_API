package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.entity.Document;

import java.util.List;

public interface AdminDocumentService {
    void updateDocumentStatus(Long documentId, Document.Status status);

    DocumentDto createDocument(DocumentCreateDto documentCreateDTO, String token);
    DocumentDto getDocument(Long id);
    List<DocumentResponseDto> getAllDocuments();
    DocumentDto updateDocument(Long id, DocumentCreateDto documentCreateDTO, String token);
    void deleteDocument(Long id);

    List<DocumentResponseDto> getDocumentsByCategoryName(String categoryName);

    // Lấy tất cả bài viết "CHƯA ĐƯỢC DUYỆT" của một user
    List<DocumentResponseDto> getDocumentsByUserIdAndStatusCreated(Long userId);

    // Lấy tất cả bài viết "ĐÃ ĐƯỢC DUYỆT" của một user
    List<DocumentResponseDto> getDocumentsByUserIdAndStatusVerified(Long userId);

    // Lấy tất cả bài viết "BỊ TỪ CHỐI" của một user
    List<DocumentResponseDto> getDocumentsByUserIdAndStatusRejected(Long userId);

    // Lấy tất cả bài viết của một user
    List<DocumentResponseDto> getAllDocumentsByUserId(Long userId);

    // Lấy danh sách tất cả các tài liệu "ĐÃ ĐƯỢC DUYỆT"
    List<DocumentResponseDto> getAllDocumentsByStatusVerified();

    // Lấy danh sách tất cả các tài liệu "CHƯA ĐƯỢC DUYỆT"
    List<DocumentResponseDto> getAllDocumentsByStatusCreated();

    // Lấy danh sách tất cả các tài liệu "BỊ TỪ CHỐI"
    List<DocumentResponseDto> getAllDocumentsByStatusRejected();

    // Sắp xếp tài liệu theo tiêu chí
    List<DocumentResponseDto> getDocumentsSorted(String sortBy, String order);

    // Tìm kiếm tài liệu theo tiêu đề
    List<DocumentResponseDto> searchDocumentsByTitle(String title);

}
