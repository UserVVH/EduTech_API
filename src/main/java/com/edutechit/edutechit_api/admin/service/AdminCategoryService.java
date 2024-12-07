package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.dto.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface AdminCategoryService {
    List<CategoryDto> getAllCategories();
    Optional<CategoryDto> getCategoryById(Long id);
    CategoryDto createCategory(CategoryDto categoryDTO);
    CategoryDto updateCategory(Long id, CategoryDto categoryDTO);
    void deleteCategory(Long categoryId);
    List<CategoryDto> getCategoryByName(String name);
}