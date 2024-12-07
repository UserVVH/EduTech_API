package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.CategoryDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.service.category.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsInCategory(
      @PathVariable Long categoryId) {
    List<DocumentResponseDto> documents = categoryService.getAllDocumentsInCategory(categoryId);
    return ResponseEntity.ok(documents);
  }

}
