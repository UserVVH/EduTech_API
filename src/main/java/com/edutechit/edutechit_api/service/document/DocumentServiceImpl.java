package com.edutechit.edutechit_api.service.document;

import com.edutechit.edutechit_api.configuration.jwt.JwtTokenProvider;
import com.edutechit.edutechit_api.dto.CommentDto;
import com.edutechit.edutechit_api.dto.DocumentCreateDto;
import com.edutechit.edutechit_api.dto.DocumentDto;
import com.edutechit.edutechit_api.dto.DocumentResponseDto;
import com.edutechit.edutechit_api.entity.*;
import com.edutechit.edutechit_api.exception.CategoryNotFoundException;
import com.edutechit.edutechit_api.exception.DocumentNotFoundException;
import com.edutechit.edutechit_api.exception.UserNotFoundException;
import com.edutechit.edutechit_api.repository.*;
import com.edutechit.edutechit_api.util.DropboxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

  @Autowired
  private DocumentRepository documentRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private DropboxUtils dropboxUtils;

  @Override
  @Transactional
  public DocumentDto createDocument(DocumentCreateDto documentCreateDTO, String token) {
    try {
      Document document = new Document();
      document.setTitle(documentCreateDTO.getTitle());
      document.setDescription(documentCreateDTO.getDescription());
      document.setAuthor(documentCreateDTO.getAuthor());
      document.setPublisher(documentCreateDTO.getPublisher());
      document.setPublishingYear(documentCreateDTO.getPublishingYear());
      document.setCreatedAt(LocalDateTime.now());
      document.setUpdatedAt(LocalDateTime.now());
      document.setStatus(Document.Status.CREATED);

      long userId = getUserIdFromToken(token);
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new UserNotFoundException("User not found"));
      document.setUser(user);

      Category category = categoryRepository.findById(documentCreateDTO.getCategoryId())
          .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
      document.setCategory(category);

      if (documentCreateDTO.getImage() != null) {
        String imageLink = saveImageToDropbox(documentCreateDTO.getImage());
        saveImageLinkToDatabase(imageLink, document);
      }

      DocumentDto documentDto = toDTO(document);
      if (documentCreateDTO.getPdfFiles() != null) {
        String pdfFileName = savePdfFileToDropbox(documentCreateDTO.getPdfFiles(), document);
        documentDto.setPdfFiles(pdfFileName);
      }
      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to create document", e);
    }
  }


  private void saveFileInfo(String url, Document document) {
    File existingFile = fileRepository.findByDocument(document);
    if (existingFile == null) {
      File file = new File();
      file.setFilename(url.substring(url.lastIndexOf('/') + 1));
      file.setFilePath(url);
      file.setCreatedAt(LocalDateTime.now());
      file.setDocument(document);
      fileRepository.save(file);
    } else {
      existingFile.setFilePath(url);
      existingFile.setFilename(url.substring(url.lastIndexOf('/') + 1));
      fileRepository.save(existingFile);
    }
  }

  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex);
  }

  private Long getUserIdFromToken(String token) {
    String email = jwtTokenProvider.getEmailFromToken(token);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
    return user.getId();
  }

  private DocumentDto toDTO(Document document) {
    DocumentDto documentDto = new DocumentDto();
    documentDto.setId(document.getId());
    documentDto.setTitle(document.getTitle());
    documentDto.setDescription(document.getDescription());
    documentDto.setAuthor(document.getAuthor());
    documentDto.setPublisher(document.getPublisher());
    documentDto.setPublishingYear(document.getPublishingYear());
    documentDto.setImage(document.getImage());
    documentDto.setCategoryName(document.getCategory().getName());
    documentDto.setUserId(document.getUser().getId());
    documentDto.setUserName(document.getUser().getFullname());
    documentDto.setCreatedAt(document.getCreatedAt());
    documentDto.setUpdatedAt(document.getUpdatedAt());
    if (document.getFile() != null) {
      documentDto.setPdfFiles(document.getFile().getFilePath());
    } else {
      documentDto.setPdfFiles(null);
    }
    documentDto.setView(document.getView());
    documentDto.setStatus(document.getStatus());
    return documentDto;
  }

  private String saveImageToDropbox(MultipartFile imageFile) {
    try (InputStream in = imageFile.getInputStream()) {
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
      String extension = getFileExtension(imageFile.getOriginalFilename());
      String filename = "image_" + timestamp + extension;

      String filePath = dropboxUtils.uploadFile(in, filename);
      return dropboxUtils.getSharedLink(filePath);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save image to Dropbox", e);
    }
  }

  private void saveImageLinkToDatabase(String imageLink, Document document) {
    document.setImage(imageLink);
    documentRepository.save(document);
  }


  @Override
  @Transactional
  public DocumentDto updateDocument(Long id, DocumentCreateDto documentCreateDTO, String token) {
    try {
      Document document = documentRepository.findById(id)
          .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
      document.setTitle(documentCreateDTO.getTitle());
      document.setDescription(documentCreateDTO.getDescription());
      document.setAuthor(documentCreateDTO.getAuthor());
      document.setPublisher(documentCreateDTO.getPublisher());
      document.setPublishingYear(documentCreateDTO.getPublishingYear());
      document.setUpdatedAt(LocalDateTime.now());

      Category category = categoryRepository.findById(documentCreateDTO.getCategoryId())
          .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
      document.setCategory(category);

      document = documentRepository.save(document);

      if (documentCreateDTO.getImage() != null) {
        String imageLink = saveImageToDropbox(documentCreateDTO.getImage());
        saveImageLinkToDatabase(imageLink, document);
      }

      DocumentDto documentDto = toDTO(document);
      if (documentCreateDTO.getPdfFiles() != null) {
        String pdfFileName = savePdfFileToDropbox(documentCreateDTO.getPdfFiles(), document);
        documentDto.setPdfFiles(pdfFileName);
        saveFileInfo(pdfFileName, document); // Update the file information in the File table
      }
      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to update document", e);
    }
  }

  private String savePdfFileToDropbox(MultipartFile pdfFile, Document document) {
    try (InputStream in = pdfFile.getInputStream()) {
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyHHmmss"));
      String extension = getFileExtension(pdfFile.getOriginalFilename());
      String filename = "pdf_" + timestamp + extension;

      String filePath = dropboxUtils.uploadFile(in, filename);
      String fileLink = dropboxUtils.getSharedLink(filePath);
      saveFileInfo(fileLink, document);
      return fileLink;
    } catch (Exception e) {
      throw new RuntimeException("Failed to save PDF to Dropbox", e);
    }
  }

  @Override
  @Transactional
  public DocumentDto getDocument(Long id) {
    try {
      Document document = documentRepository.findById(id)
          .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
      document.setView(document.getView() + 1);
      document = documentRepository.save(document);

      DocumentDto documentDto = toDTO(document);

      List<Comment> comments = commentRepository.findCommentsByDocumentId(id);
      Set<CommentDto> commentDtos = comments.stream()
          .map(this::toCommentDto)
          .collect(Collectors.toSet());

      documentDto.setComments(commentDtos);
      documentDto.setCategoryName(document.getCategory().getName());
      documentDto.setPdfFiles(
          documentRepository.findFilePathsByDocumentId(document.getId()).get(0));

      return documentDto;
    } catch (Exception e) {
      throw new RuntimeException("Failed to get document", e);
    }
  }

  private CommentDto toCommentDto(Comment comment) {
    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setContent(comment.getContent());
    commentDto.setUserId(comment.getUser().getId());
    commentDto.setUserName(comment.getUser().getFullname());
    commentDto.setAvatar(comment.getUser().getAvatar());
    commentDto.setDocumentId(comment.getDocument().getId());
    commentDto.setCreatedAt(comment.getCreatedAt());
    commentDto.setUpdatedAt(comment.getUpdatedAt());

    return commentDto;
  }

  //getAllDocuments VERIFIED
  @Override
  public List<DocumentResponseDto> getAllDocuments() {
    List<Document> documents = documentRepository.findAllDocuments();
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Transactional
  public void deleteDocument(Long documentId) {
    Document document = documentRepository.findById(documentId)
        .orElseThrow(
            () -> new DocumentNotFoundException("Document not found with id: " + documentId));

    // First delete all comments and file related to the document
    fileRepository.deleteByDocument(document);
    commentRepository.deleteByDocumentId(documentId);

    // Now delete the document
    documentRepository.delete(document);
  }

  private DocumentResponseDto toResponseDTO(Document document) {
    DocumentResponseDto documentResponseDto = new DocumentResponseDto();
    documentResponseDto.setId(document.getId());
    documentResponseDto.setTitle(document.getTitle());
    documentResponseDto.setAuthor(document.getAuthor());
    documentResponseDto.setPublisher(document.getPublisher());
    documentResponseDto.setPublishingYear(document.getPublishingYear());
    documentResponseDto.setImage(document.getImage());
    documentResponseDto.setCategoryName(document.getCategory().getName());
    documentResponseDto.setUserName(document.getUser().getFullname());
    documentResponseDto.setCreatedAt(document.getCreatedAt());
    documentResponseDto.setUpdatedAt(document.getUpdatedAt());
    documentResponseDto.setView(document.getView());
    documentResponseDto.setStatus(document.getStatus());
    return documentResponseDto;
  }

  @Override
  public List<DocumentResponseDto> getDocumentsByCategoryName(String categoryName) {
    List<Document> documents = documentRepository.findAllDocumentsByCategoryName(categoryName);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //tìm tất cả các document của user khác có status là VERIFIED
  @Override
  public List<DocumentResponseDto> getDocumentsByUserIdAndVERIFIED(Long userId) {
    List<Document> documents = documentRepository.findDocumentsByUserIdAndVERIFIED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Override
  public List<DocumentResponseDto> getDocumentsByUserIdAndCREATED(Long userId) {
    List<Document> documents = documentRepository.findDocumentsByUserIdAndCREATED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //tìm tất cả các document của bản thân có status là VERIFIED
  @Override
  public List<DocumentResponseDto> getDocumentsOfMyAndVERIFIED(String token) {
    Long userId = getUserIdFromToken(token);
    List<Document> documents = documentRepository.findDocumentsByUserIdAndVERIFIED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //tìm tất cả các document của bản thân có status là CREATE
  @Override
  public List<DocumentResponseDto> getDocumentsOfMyAndCREATED(String token) {
    Long userId = getUserIdFromToken(token);
    List<Document> documents = documentRepository.findDocumentsByUserIdAndCREATED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //tìm tất cả các document của bản thân có status là REJECT
  @Override
  public List<DocumentResponseDto> getDocumentsOfMyAndREJECTED(String token) {
    Long userId = getUserIdFromToken(token);
    List<Document> documents = documentRepository.findDocumentsByUserIdAndREJECTED(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //Lấy tất cả bài viết của bản thân
  @Override
  public List<DocumentResponseDto> getAllDocumentsOfMy(String token) {
    Long userId = getUserIdFromToken(token);
    List<Document> documents = documentRepository.findAllOfMy(userId);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //sắp xếp tài liệu dựa trên query string
  @Override
  public List<DocumentResponseDto> getDocumentsSorted(String sortBy, String order) {
    List<Document> documents = documentRepository.findAllSorted(sortBy, order);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  //seach document theo title
  @Override
  public List<DocumentResponseDto> searchDocumentsByTitle(String title) {
    List<Document> documents = documentRepository.findByTitleContainingIgnoreCase(title);
    return documents.stream().map(this::toResponseDTO).collect(Collectors.toList());
  }

  @Override
  public List<DocumentResponseDto> getTop10NewestVerifiedDocuments() {
    List<Document> documents = documentRepository.findTop10ByOrderByCreatedAtDesc();
    return documents.stream().map(this::toResponseDTO).toList();
  }

  // Top 100 bài viết VERIFIED có số view cao nhất
  @Override
  public List<DocumentResponseDto> getTop100MostViewedVerifiedDocuments() {
    List<Document> documents = documentRepository.findTop100ByOrderByViewDesc();
    return documents.stream().map(this::toResponseDTO).toList();
  }

  // Top 10 bài viết VERIFIED có số view cao nhất
  @Override
  public List<DocumentResponseDto> getTop10MostViewedVerifiedDocuments() {
    List<Document> documents = documentRepository.findTop10ByOrderByViewDesc();
    return documents.stream().map(this::toResponseDTO).toList();
  }
}