package com.edutechit.edutechit_api.controller;

import ch.qos.logback.classic.Logger;
import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.service.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    private Logger log;

    // Create a new document
    // Tạo mới document
    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@Valid @ModelAttribute DocumentCreateDto documentCreateDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(documentService.createDocument(documentCreateDTO, token));
    }


    // Get the document by ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocument(id)); // Trả về thông tin của document có id trùng với id truyền vào
    }

    // Get all the documents VERIFIED
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments()); // Trả về danh sách tất cả các document
    }

    // Cập nhật document
    @PutMapping("/{id}")
    public ResponseEntity<DocumentDto> updateDocument(@PathVariable Long id, @ModelAttribute DocumentCreateDto documentCreateDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(documentService.updateDocument(id, documentCreateDTO, token));
    }

    // Delete the document
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok("Document deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Get documents by category name
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByCategoryName(@PathVariable String categoryName) {
        List<DocumentResponseDto> documents = documentService.getDocumentsByCategoryName(categoryName);
        return ResponseEntity.ok(documents);
    }

    // Get documents by user ID and status VERIFIED
    @GetMapping("/user/{userId}/verified")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserIdAndVERIFIED(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = documentService.getDocumentsByUserIdAndVERIFIED(userId);
        return ResponseEntity.ok(documents);
    }

    // Get documents by user ID and status CREATED
    @GetMapping("/user/{userId}/created")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserIdAndCREATED(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = documentService.getDocumentsByUserIdAndCREATED(userId);
        return ResponseEntity.ok(documents);
    }

    // Get documents of the current user with status VERIFIED
    @GetMapping("/my/verified")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsOfMyAndVERIFIED(@RequestHeader("Authorization") String token) {
        List<DocumentResponseDto> documents = documentService.getDocumentsOfMyAndVERIFIED(token);
        return ResponseEntity.ok(documents);
    }

    // Get documents of the current user with status CREATED
    @GetMapping("/my/created")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsOfMyAndCREATED(@RequestHeader("Authorization") String token) {
        List<DocumentResponseDto> documents = documentService.getDocumentsOfMyAndCREATED(token);
        return ResponseEntity.ok(documents);
    }

    // Get documents of the current user with status CREATED
    @GetMapping("/my/rejected")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsOfMyAndREJECTED(@RequestHeader("Authorization") String token) {
        List<DocumentResponseDto> documents = documentService.getDocumentsOfMyAndREJECTED(token);
        return ResponseEntity.ok(documents);
    }

    // Get all documents of the current user
    @GetMapping("/my")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsOfMy(@RequestHeader("Authorization") String token) {
        List<DocumentResponseDto> documents = documentService.getAllDocumentsOfMy(token);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsSorted(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        List<DocumentResponseDto> documents = documentService.getDocumentsSorted(sortBy, order);
        return ResponseEntity.ok(documents);
    }

    // Search documents by title
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponseDto>> searchDocumentsByTitle(@RequestParam String title) {
        List<DocumentResponseDto> documents = documentService.searchDocumentsByTitle(title);
        return ResponseEntity.ok(documents);
    }

    // Lấy top 10 bài viết VERIFIED mới nhất
    @GetMapping("/top-10-newest")
    public ResponseEntity<List<DocumentResponseDto>> getTop10NewestVerifiedDocuments() {
        List<DocumentResponseDto> documents = documentService.getTop10NewestVerifiedDocuments();
        return ResponseEntity.ok(documents);
    }

    // Lấy top 100 bài viết VERIFIED có số view cao nhất
    @GetMapping("/top-100-most-viewed")
    public ResponseEntity<List<DocumentResponseDto>> getTop100MostViewedVerifiedDocuments() {
        List<DocumentResponseDto> documents = documentService.getTop100MostViewedVerifiedDocuments();
        return ResponseEntity.ok(documents);
    }

    // Lấy top 10 bài viết VERIFIED có số view cao nhất
    @GetMapping("/top-10-most-viewed")
    public ResponseEntity<List<DocumentResponseDto>> getTop10MostViewedVerifiedDocuments() {
        List<DocumentResponseDto> documents = documentService.getTop10MostViewedVerifiedDocuments();
        return ResponseEntity.ok(documents);
    }
}
