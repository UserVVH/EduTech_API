package com.edutechit.edutechit_api.service.document;

import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;

import java.util.List;

public interface DocumentService {
    DocumentDto createDocument(DocumentCreateDto documentCreateDTO, String token);
    DocumentDto getDocument(Long id);
    List<DocumentResponseDto> getAllDocuments();
    DocumentDto updateDocument(Long id, DocumentCreateDto documentCreateDTO, String token);
    void deleteDocument(Long id);

    List<DocumentResponseDto> getDocumentsByCategoryName(String categoryName);
    List<DocumentResponseDto> getDocumentsByUserIdAndVERIFIED(Long userId);
    List<DocumentResponseDto> getDocumentsByUserIdAndCREATED(Long userId);
    List<DocumentResponseDto> getDocumentsOfMyAndVERIFIED(String token);
    List<DocumentResponseDto> getDocumentsOfMyAndCREATED(String token);
    List<DocumentResponseDto> getDocumentsOfMyAndREJECTED(String token);
    List<DocumentResponseDto> getAllDocumentsOfMy(String token);
    List<DocumentResponseDto> getDocumentsSorted(String sortBy, String order);
    List<DocumentResponseDto> searchDocumentsByTitle(String title);

    // Lấy top 10 bài viết VERIFIED mới nhất
    List<DocumentResponseDto> getTop10NewestVerifiedDocuments();

    // Lấy top 100 bài viết VERIFIED có số view cao nhất
    List<DocumentResponseDto> getTop100MostViewedVerifiedDocuments();

    // Lấy top 10 bài viết VERIFIED có số view cao nhất
    List<DocumentResponseDto> getTop10MostViewedVerifiedDocuments();
}
