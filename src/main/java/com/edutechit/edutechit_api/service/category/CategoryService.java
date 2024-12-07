package com.edutechit.edutechit_api.service.category;

import com.edutechit.edutechit_api.dto.CategoryDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import java.util.List;

public interface CategoryService {

  List<CategoryDto> getAllCategories();

  List<DocumentResponseDto> getAllDocumentsInCategory(Long categoryId);
}
