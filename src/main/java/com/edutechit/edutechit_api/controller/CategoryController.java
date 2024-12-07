package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.CategoryDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.service.category.CategoryService;
import com.edutechit.edutechit_api.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // Lấy danh sách tất cả các category
    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Lấy danh sách tất cả các document trong category
    @GetMapping("/{categoryId}/documents")
    public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsInCategory(@PathVariable Long categoryId) {
        List<DocumentResponseDto> documents = categoryService.getAllDocumentsInCategory(categoryId);
        return ResponseEntity.ok(documents);
    }

}
