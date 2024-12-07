package com.edutechit.edutechit_api.admin.controller;

import com.edutechit.edutechit_api.admin.service.AdminDocumentService;
import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/documents")
public class AdminDocumentController {

    @Autowired
    private AdminDocumentService adminDocumentService;

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateDocumentStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Document.Status documentStatus = Document.Status.valueOf(status.toUpperCase());
            adminDocumentService.updateDocumentStatus(id, documentStatus);
            return ResponseEntity.ok("Document status updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Document not found with id " + id);
        }
    }

    // Tạo mới document
    @PostMapping
    public ResponseEntity<DocumentDto> createDocument(@Valid @ModelAttribute DocumentCreateDto documentCreateDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(adminDocumentService.createDocument(documentCreateDTO, token));
    }


    // Get the document by ID
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(adminDocumentService.getDocument(id)); // Trả về thông tin của document có id trùng với id truyền vào
    }

    // Get all the documents
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments() {
        return ResponseEntity.ok(adminDocumentService.getAllDocuments()); // Trả về danh sách tất cả các document
    }

    // Cập nhật document
    @PutMapping("/{id}")
    public ResponseEntity<DocumentDto> updateDocument(@PathVariable Long id, @ModelAttribute DocumentCreateDto documentCreateDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(adminDocumentService.updateDocument(id, documentCreateDTO, token));
    }

    // Delete the document
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        try {
            adminDocumentService.deleteDocument(id);
            return ResponseEntity.ok("Document deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Get documents by category name
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByCategoryName(@PathVariable String categoryName) {
        List<DocumentResponseDto> documents = adminDocumentService.getDocumentsByCategoryName(categoryName);
        return ResponseEntity.ok(documents);
    }

    // Get all documents sorted by a field
    @GetMapping("/sorted")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsSorted(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        List<DocumentResponseDto> documents = adminDocumentService.getDocumentsSorted(sortBy, order);
        return ResponseEntity.ok(documents);
    }

    // Search documents by title
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponseDto>> searchDocumentsByTitle(@RequestParam String title) {
        List<DocumentResponseDto> documents = adminDocumentService.searchDocumentsByTitle(title);
        return ResponseEntity.ok(documents);
    }

    // Lấy tất cả bài viết "CHƯA ĐƯỢC DUYỆT" của một user
    @GetMapping("/user/{userId}/created")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserIdAndStatusCreated(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = adminDocumentService.getDocumentsByUserIdAndStatusCreated(userId);
        return ResponseEntity.ok(documents);
    }

    // Lấy tất cả bài viết "ĐÃ ĐƯỢC DUYỆT" của một user
    @GetMapping("/user/{userId}/verified")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserIdAndStatusVerified(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = adminDocumentService.getDocumentsByUserIdAndStatusVerified(userId);
        return ResponseEntity.ok(documents);
    }

    // Lấy tất cả bài viết "BỊ TỪ CHỐI" của một user
    @GetMapping("/user/{userId}/rejected")
    public ResponseEntity<List<DocumentResponseDto>> getDocumentsByUserIdAndStatusRejected(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = adminDocumentService.getDocumentsByUserIdAndStatusRejected(userId);
        return ResponseEntity.ok(documents);
    }

    // Lấy tất cả bài viết của một user (bất kể trạng thái)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsByUserId(@PathVariable Long userId) {
        List<DocumentResponseDto> documents = adminDocumentService.getAllDocumentsByUserId(userId);
        return ResponseEntity.ok(documents);
    }

    // Lấy danh sách tất cả các tài liệu "ĐÃ ĐƯỢC DUYỆT"
    @GetMapping("/verified")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsByStatusVerified() {
        List<DocumentResponseDto> documents = adminDocumentService.getAllDocumentsByStatusVerified();
        return ResponseEntity.ok(documents);
    }

    // Lấy danh sách tất cả các tài liệu "CHƯA ĐƯỢC DUYỆT"
    @GetMapping("/created")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsByStatusCreated() {
        List<DocumentResponseDto> documents = adminDocumentService.getAllDocumentsByStatusCreated();
        return ResponseEntity.ok(documents);
    }

    // Lấy danh sách tất cả các tài liệu "BỊ TỪ CHỐI"
    @GetMapping("/rejected")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsByStatusRejected() {
        List<DocumentResponseDto> documents = adminDocumentService.getAllDocumentsByStatusRejected();
        return ResponseEntity.ok(documents);
    }
}

