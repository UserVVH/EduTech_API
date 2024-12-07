package com.edutechit.edutechit_api.admin.service;

import com.edutechit.edutechit_api.dto.CategoryDto;
import com.edutechit.edutechit_api.entity.Category;
import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.exception.ResourceNotFoundException;
import com.edutechit.edutechit_api.repository.CategoryRepository;
import com.edutechit.edutechit_api.repository.CommentRepository;
import com.edutechit.edutechit_api.repository.DocumentRepository;
import com.edutechit.edutechit_api.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private FileRepository fileRepository;

  @Override
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(this::convertToCategoryDto)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CategoryDto> getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .map(this::convertToCategoryDto);
  }

  @Override
  @Transactional
  public CategoryDto createCategory(CategoryDto categoryDTO) {
    Category category = new Category();
    category.setName(categoryDTO.getName());
    category = categoryRepository.save(category);
    return convertToCategoryDto(category);
  }

  @Override
  @Transactional
  public CategoryDto updateCategory(Long id, CategoryDto categoryDTO) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    category.setName(categoryDTO.getName());
    category = categoryRepository.save(category);
    return convertToCategoryDto(category);
  }

  @Override
  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Category not found with id: " + categoryId));

    // Delete all documents, comments, and files associated with the category
    List<Document> documents = documentRepository.findByCategoryId(categoryId);
    for (Document document : documents) {
      fileRepository.deleteByDocument(document);
      commentRepository.deleteByDocumentId(document.getId());
      documentRepository.delete(document);
    }

    categoryRepository.delete(category);
  }

  @Override
  public List<CategoryDto> getCategoryByName(String name) {
    return categoryRepository.findByNameContainingIgnoreCase(name).stream()
        .map(this::convertToCategoryDto)
        .collect(Collectors.toList());
  }

  private CategoryDto convertToCategoryDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    return dto;
  }
}