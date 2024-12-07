package com.edutechit.edutechit_api.service.category;

import com.edutechit.edutechit_api.dto.CategoryDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.entity.Category;
import com.edutechit.edutechit_api.entity.Document;
import com.edutechit.edutechit_api.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;


  // Lấy danh sách tất cả các category
  @Override
  public List<CategoryDto> getAllCategories() {
    return categoryRepository.findAll().stream()
        .map(this::convertToCategoryDto)
        .collect(Collectors.toList()); // chuyển danh sách category thành danh sách categoryDto
  }

  // Lấy danh sách tất cả các document trong category
  @Override
  public List<DocumentResponseDto> getAllDocumentsInCategory(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new RuntimeException(
            "Category not found")); // nếu không tìm thấy category thì ném ra ngoại lệ
    return category.getDocuments().stream()
        .map(this::convertToDocumentDto)
        .collect(Collectors.toList()); // chuyển danh sách document thành danh sách documentDto
  }

  // Chuyển đổi từ Category sang CategoryDto
  private CategoryDto convertToCategoryDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setId(category.getId());
    dto.setName(category.getName());
    return dto;
  }

  // Chuyển đổi từ Document sang DocumentResponseDto
  private DocumentResponseDto convertToDocumentDto(Document document) {
    DocumentResponseDto dto = new DocumentResponseDto();
    dto.setId(document.getId());
    dto.setTitle(document.getTitle());
    dto.setAuthor(document.getAuthor());
    dto.setPublisher(document.getPublisher());
    dto.setPublishingYear(document.getPublishingYear());
    dto.setImage(document.getImage());
    dto.setCategoryName(document.getCategory().getName());
    dto.setUserName(document.getUser().getFullname());
    dto.setCreatedAt(document.getCreatedAt());
    dto.setUpdatedAt(document.getUpdatedAt());
    dto.setView(document.getView());
    dto.setStatus(document.getStatus());
    return dto;
  }
}
