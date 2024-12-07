package com.edutechit.edutechit_api.admin.controller;

import com.edutechit.edutechit_api.admin.service.AdminCategoryService;
import com.edutechit.edutechit_api.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

  @Autowired
  private AdminCategoryService categoryService;

  //get all categories
  @GetMapping
  public ResponseEntity<List<CategoryDto>> getAllCategories() {
    List<CategoryDto> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
    Optional<CategoryDto> category = categoryService.getCategoryById(id);
    return category.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  //add category
  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDTO) {
    CategoryDto createdCategory = categoryService.createCategory(categoryDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,
      @RequestBody CategoryDto categoryDTO) {
    try {
      CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDTO);
      return ResponseEntity.ok(updatedCategory);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    try {
      categoryService.deleteCategory(id);
      return ResponseEntity.noContent().build();
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @GetMapping("/search")
  public ResponseEntity<List<CategoryDto>> searchCategories(@RequestParam String name) {
    List<CategoryDto> categories = categoryService.getCategoryByName(name);
    return ResponseEntity.ok(categories);
  }
}