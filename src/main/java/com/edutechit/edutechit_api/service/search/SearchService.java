package com.edutechit.edutechit_api.service.search;

import com.edutechit.edutechit_api.dto.*;
import com.edutechit.edutechit_api.entity.*;
import com.edutechit.edutechit_api.repository.UserRepository;
import com.edutechit.edutechit_api.repository.DocumentRepository;
import com.edutechit.edutechit_api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public SearchResponseDto search(String searchText) {
        SearchResponseDto response = new SearchResponseDto();

        // Search users by username
        List<User> users = userRepository.findByFullnameContainingIgnoreCase(searchText);
        List<UserInfoDto> userDtos = users.stream().map(this::convertToUserInfoDto).collect(Collectors.toList());
        response.setUsers(userDtos);

        // Search documents by title, username, category name, author, publisher, publishing year
        List<Document> documents = documentRepository.findByTitleContainingIgnoreCaseOrUser_FullnameContainingIgnoreCaseOrCategory_NameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrPublisherContainingIgnoreCaseOrPublishingYearContainingIgnoreCase(
                searchText);
        List<DocumentResponseDto> documentDtos = documents.stream().map(this::convertToDocumentDto).collect(Collectors.toList());
        response.setDocumentsByTitle(documentDtos); // Reusing the same field for simplicity

        // Search categories by name
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(searchText);
        List<CategoryDto> categoryDtos = categories.stream().map(this::convertToCategoryDto).collect(Collectors.toList());
        response.setCategoryDtos(categoryDtos);

        return response;
    }

    private UserInfoDto convertToUserInfoDto(User user) {
        UserInfoDto dto = new UserInfoDto();
        dto.setFullname(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setIdentifier(user.getIdentifier());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setEnabled(user.getEnabled());
        dto.setRole(user.getRole().getName());
        return dto;
    }

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

    private CategoryDto convertToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}